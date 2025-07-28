import os
import random

from PyQt5.QtCore import Qt, QPropertyAnimation, QParallelAnimationGroup, QSequentialAnimationGroup, QRect
from PyQt5.QtWidgets import QWidget, QGraphicsOpacityEffect, QLabel
from PyQt5.QtGui import QPixmap, QPalette, QColor
from loguru import logger

from config.kamehouse_desktop_cfg import kamehouseDesktopCfg
from widgets.image import ImageWidget

class BackgroundSlideshowWidget(QWidget):
    window = None
    logTrace = False
    screenWidth = 0
    screenHeight = 0
    expandPx = 0
    backgroundImages = []
    defaultBackgroundImages = []
    randomImage = None
    
    def __init__(self, window):
        super().__init__(window)
        logger.info("Initializing background_slideshow_widget")
        if (kamehouseDesktopCfg.getBoolean('background_slideshow_widget', 'hidden')):
            logger.debug("background_slideshow_widget is set to hidden")
            self.setHidden(True)
            return
        self.window = window
        self.logTrace = kamehouseDesktopCfg.getBoolean('background_slideshow_widget', 'trace_log_enabled')
        self.logBackgroundImages = kamehouseDesktopCfg.getBoolean('background_slideshow_widget', 'log_background_images')
        self.expandPx = kamehouseDesktopCfg.getInt('background_slideshow_widget', 'expand_px')
        self.setScreenSize()
        self.setBackgroundColor()
        self.background = ImageWidget("background_slideshow_image_widget", window)
        self.setBackgroundPriority()
        self.setBackgroundImagesList()
        self.selectRandomBackgroundImage()
        self.setBackgroundAnimation()
        self.startBackgroundAnimation()

    def setScreenSize(self):
        screen = self.window.app.primaryScreen()
        screenSize = screen.size()
        self.screenWidth = screenSize.width()
        self.screenHeight = screenSize.height()

    def setBackgroundColor(self):
        self.backgroundColor = QLabel(self.window)
        self.backgroundColor.setStyleSheet(kamehouseDesktopCfg.get('background_slideshow_widget', 'stylesheet'))
        self.backgroundColor.setGeometry(0, 0, self.screenWidth, self.screenHeight)

    def setBackgroundPriority(self):
        self.background.lower()
        self.backgroundColor.lower()
        self.backgroundColor.lower()

    def setBackgroundImagesList(self):
        defaultBackgroundImagesPath = "lib/ui/img/banners"
        for root, _, files in os.walk(defaultBackgroundImagesPath):
            for file in files:
                fullPath = os.path.join(root, file).replace("\\", "/")
                if (self.isValidImageFile(fullPath)):
                    self.defaultBackgroundImages.append(fullPath)
        userHome = os.path.expanduser("~").replace("\\", "/")
        imagesSrcPath = kamehouseDesktopCfg.get('background_slideshow_widget', 'images_src_path')
        backgroundImagesPath = userHome + imagesSrcPath
        for root, _, files in os.walk(backgroundImagesPath):
            for file in files:
                fullPath = os.path.join(root, file).replace("\\", "/")
                if (self.isValidImageFile(fullPath)):
                    self.backgroundImages.append(fullPath)
        if (self.logTrace and self.logBackgroundImages):
            logger.trace("background images path: " + backgroundImagesPath)
            logger.trace(self.backgroundImages)

    def isValidImageFile(self, imagePath):
        if (imagePath is None):
            return False
        return (imagePath.endswith(".jpeg") or 
                 imagePath.endswith(".JPEG") or 
                 imagePath.endswith(".jpg") or 
                 imagePath.endswith(".JPG") or 
                 imagePath.endswith(".bmp") or 
                 imagePath.endswith(".BMP") or 
                 imagePath.endswith(".png") or 
                 imagePath.endswith(".PNG") or 
                 imagePath.endswith(".webp") or 
                 imagePath.endswith(".WEBP"))

    def setBackgroundAnimation(self):
        animationMs = kamehouseDesktopCfg.getInt('background_slideshow_widget', 'animation_ms')
        minOpacity = kamehouseDesktopCfg.getFloat('background_slideshow_widget', 'min_opacity')
        maxOpacity = kamehouseDesktopCfg.getFloat('background_slideshow_widget', 'max_opacity')
        # contract animation
        self.background.contract = QPropertyAnimation(self.background, b"geometry")
        self.background.contract.setDuration(animationMs)
        # expand animation
        self.background.expand = QPropertyAnimation(self.background, b"geometry")
        self.background.expand.setDuration(animationMs)
        self.configureExpandContractParameters()
        # brighten
        effect = QGraphicsOpacityEffect(self.background)
        self.background.setGraphicsEffect(effect)
        self.background.brighten = QPropertyAnimation(effect, b"opacity")
        self.background.brighten.setStartValue(minOpacity)
        self.background.brighten.setEndValue(maxOpacity)
        self.background.brighten.setDuration(animationMs)
        # darken
        self.background.darken = QPropertyAnimation(effect, b"opacity")
        self.background.darken.setStartValue(maxOpacity)
        self.background.darken.setEndValue(minOpacity)
        self.background.darken.setDuration(animationMs)
        # animation groups
        self.background.contractBrighten = QParallelAnimationGroup()
        self.background.contractBrighten.addAnimation(self.background.contract)
        self.background.contractBrighten.addAnimation(self.background.brighten)
        self.background.expandDarken = QParallelAnimationGroup()
        self.background.expandDarken.addAnimation(self.background.expand)
        self.background.expandDarken.addAnimation(self.background.darken)
        self.background.animGroup = QSequentialAnimationGroup()
        self.background.animGroup.addAnimation(self.background.contractBrighten)
        self.background.animGroup.addAnimation(self.background.expandDarken)
        self.background.animGroup.finished.connect(self.restartBackgroundAnimation)

    def startBackgroundAnimation(self):
        self.background.animGroup.start()

    def restartBackgroundAnimation(self):
        if (self.logTrace):
            logger.trace("Restarting background slideshow animation")
        self.selectRandomBackgroundImage()
        self.configureExpandContractParameters()
        self.background.animGroup.start()

    def selectRandomBackgroundImage(self):
        if (len(self.backgroundImages) > 0):
            self.randomImage = random.choice(self.backgroundImages)
        else:
            if (self.logTrace):
                logger.trace("Configured source is invalid or empty, setting background from default images")
            self.randomImage = random.choice(self.defaultBackgroundImages)
        self.background.imgSrc = QPixmap(self.randomImage)
        self.background.setPixmap(self.background.imgSrc)

    def configureExpandContractParameters(self):
        posX = 0
        posY = 0
        expandedPosX = 0
        expandedPosY = 0
        width = 0
        height = 0
        expandedWidth = 0
        expandedHeight = 0
        aspectRatio = 1
        imageWidth = self.background.imgSrc.width()
        imageHeight = self.background.imgSrc.height()
        if (imageWidth > 0 and imageHeight > 0):
            aspectRatio = imageWidth / imageHeight
        if (self.logTrace and self.logBackgroundImages):
            logger.trace(self.randomImage)
            logger.trace("width: " + str(imageWidth) + ", height: " + str(imageHeight) + ", ar: " + str(aspectRatio))
        if (aspectRatio >= 1):
            width = self.screenWidth
            height = self.screenHeight
            expandedPosX = posX - self.expandPx
            expandedPosY = posY - self.expandPx
            expandedWidth = width + self.expandPx * 2 
            expandedHeight = height + self.expandPx * 2
        else:
            width = int(self.screenHeight * aspectRatio)
            height = self.screenHeight
            posX = int((self.screenWidth / 2) - (width / 2))
            posY = 0
            expandedPosX = posX - self.expandPx
            expandedPosY = posY - self.expandPx
            expandedWidth = width + self.expandPx * 2 
            expandedHeight = height + self.expandPx * 2
        # set contract parameters
        self.background.contract.setStartValue(QRect(expandedPosX, expandedPosY, expandedWidth, expandedHeight))
        self.background.contract.setEndValue(QRect(posX, posY, width, height))
        # set expand parameters
        self.background.expand.setStartValue(QRect(posX, posY, width, height))
        self.background.expand.setEndValue(QRect(expandedPosX, expandedPosY, expandedWidth, expandedHeight))
