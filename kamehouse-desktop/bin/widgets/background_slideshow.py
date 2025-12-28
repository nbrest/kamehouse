import os
import random

from PyQt5.QtCore import Qt, QPropertyAnimation, QParallelAnimationGroup, QSequentialAnimationGroup, QRect, QSize
from PyQt5.QtWidgets import QWidget, QGraphicsOpacityEffect, QLabel
from PyQt5.QtGui import QPixmap, QPalette, QColor, QImageReader, QPainter
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
    portraitBackgroundImages = []
    randomImage = None
    userHome = None
    backgroundsSuccessListFile = "/.kamehouse/data/desktop/backgrounds-success.list"
    backgroundsErrorListFile = "/.kamehouse/data/desktop/backgrounds-error.list"
    
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
                    image = self.getBackgroundImage(fullPath)
                    if image is not None:
                        self.defaultBackgroundImages.append(image)
                        if (image.getPortrait()):
                            self.portraitBackgroundImages.append(image)
        self.userHome = os.path.expanduser("~").replace("\\", "/")
        imagesSrcPath = kamehouseDesktopCfg.get('background_slideshow_widget', 'images_src_path')
        backgroundImagesPath = self.userHome + imagesSrcPath
        for root, _, files in os.walk(backgroundImagesPath):
            for file in files:
                fullPath = os.path.join(root, file).replace("\\", "/")
                if (self.isValidImageFile(fullPath)):
                    image = self.getBackgroundImage(fullPath)
                    if image is not None:
                        self.backgroundImages.append(image)
                        if (image.getPortrait()):
                            self.portraitBackgroundImages.append(image)
        if (self.logTrace and self.logBackgroundImages):
            logger.trace("background images path: " + backgroundImagesPath)
            logger.trace(self.backgroundImages)

    def isValidImageFile(self, imagePath):
        if (imagePath is None):
            return False
        imagePathLower = imagePath.lower()
        return (imagePathLower.endswith(".jpeg") or 
                 imagePathLower.endswith(".jpg") or 
                 imagePathLower.endswith(".heic") or 
                 imagePathLower.endswith(".bmp") or 
                 imagePathLower.endswith(".png") or 
                 imagePathLower.endswith(".webp"))

    def getBackgroundImage(self, imagePath):
        image = BackgroundImage()
        image.setFilename(imagePath)
        imageReader = QImageReader(imagePath)
        if not imageReader.canRead():
            logger.error("Can't read image: " + imagePath)
            self.updateInvalidBackgroundImageListFile(imagePath)
            return None
        size = imageReader.size() 
        if not size.isValid():
            logger.error("Invalid size for image: " + imagePath)
            self.updateInvalidBackgroundImageListFile(imagePath)
            return None
        width = size.width()
        height = size.height()
        image.setWidth(width)
        image.setHeight(height)
        if (width <= 0 or height <= 0):
            logger.error("Width or height are <= 0 for image: " + imagePath)
            self.updateInvalidBackgroundImageListFile(imagePath)
            return None
        if (width / height <= 1):
            image.setPortrait(True)
        return image

    def updateInvalidBackgroundImageListFile(self, imagePath):
        if (self.logTrace):
            logger.error("Invalid image " + imagePath)
        self.updateBackgroundImageListFile(self.backgroundsErrorListFile, imagePath)
        return

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
        self.randomImage = self.getRandomImage()
        if (self.randomImage.getPortrait()):
            secondPortraitImage = self.getSecondPortraitImage(self.randomImage.getFilename())
            pixmap1 = QPixmap(self.randomImage.getFilename())
            pixmap2 = QPixmap(secondPortraitImage.getFilename()).scaled(pixmap1.width(), pixmap1.height())
            portraitSeparatorPx = kamehouseDesktopCfg.getInt('background_slideshow_widget', 'portrait_separator_px')
            totalWidth = pixmap1.width() + pixmap2.width() + portraitSeparatorPx
            combinedPixmap = QPixmap(totalWidth, pixmap1.height())
            combinedPixmap.fill(Qt.transparent)
            painter = QPainter(combinedPixmap)
            painter.drawPixmap(0, 0, pixmap1)               
            painter.drawPixmap(pixmap1.width() + portraitSeparatorPx, 0, pixmap2)
            painter.end()
            self.updateBackgroundImageListFile(self.backgroundsSuccessListFile, self.randomImage.getFilename())
            self.updateBackgroundImageListFile(self.backgroundsSuccessListFile, secondPortraitImage.getFilename())
            self.background.imgSrc = combinedPixmap
            self.background.setPixmap(self.background.imgSrc)
        else:
            pixmap = QPixmap(self.randomImage.getFilename())
            self.updateBackgroundImageListFile(self.backgroundsSuccessListFile, self.randomImage.getFilename())
            self.background.imgSrc = pixmap
            self.background.setPixmap(self.background.imgSrc)

    def useDefaultBackgroundImages(self):
        return len(self.backgroundImages) <= 0

    def getRandomImage(self):
        if (self.useDefaultBackgroundImages()):
            if (self.logTrace):
                logger.trace("Configured source is invalid or empty, setting background from default images")
            return random.choice(self.defaultBackgroundImages)
        else:
           return random.choice(self.backgroundImages)

    def getSecondPortraitImage(self, currentFilePath):
        filteredImages = [image for image in self.portraitBackgroundImages if (not image.getFilename() == currentFilePath)]
        return random.choice(filteredImages)

    def updateBackgroundImageListFile(self, filePath, currentFilePath):
        if (kamehouseDesktopCfg.getBoolean('background_slideshow_widget', 'skip_update_backgrounds_list_files')):
            return
        backgroundsFile = self.userHome + filePath
        try:
            backgroundsList = []
            with open(backgroundsFile, 'r') as file:
                backgroundsList = [line.strip() for line in file]
            if currentFilePath not in backgroundsList:
                with open(backgroundsFile, 'a') as file:
                    file.write(currentFilePath + "\n")
        except IOError as error:
            logger.error("Error updating background images list file " + backgroundsFile)
        
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
        else:
            if (self.logTrace):
                logger.error("Invalid image properties for " + self.randomImage.getFilename())
        if (self.logTrace and self.logBackgroundImages):
            logger.trace(self.randomImage.getFilename())
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

class BackgroundImage():
    filename = None
    isPortrait = False
    width = 0
    height = 0

    def __init__(self):
        return

    def getFilename(self):
        return self.filename

    def setFilename(self, value):
        self.filename = value

    def getPortrait(self):
        return self.isPortrait

    def setPortrait(self, value):
        self.isPortrait = value

    def getWidth(self):
        return self.width

    def setWidth(self, value):
        self.width = value    

    def getHeight(self):
        return self.height

    def setHeight(self, value):
        self.height = value  
