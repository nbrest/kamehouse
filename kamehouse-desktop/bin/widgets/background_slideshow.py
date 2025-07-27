from PyQt5.QtCore import Qt, QPropertyAnimation, QParallelAnimationGroup, QSequentialAnimationGroup, QRect
from PyQt5.QtWidgets import QWidget, QGraphicsOpacityEffect
from PyQt5.QtGui import QPixmap
from loguru import logger

from config.kamehouse_desktop_cfg import kamehouseDesktopCfg
from widgets.image import ImageWidget

class BackgroundSlideshowWidget(QWidget):
    window = None
    logTrace = False
    screenWidth = 0
    screenHeight = 0
    
    def __init__(self, window):
        super().__init__(window)
        logger.info("Initializing background_slideshow_widget")
        if (kamehouseDesktopCfg.getBoolean('background_slideshow_widget', 'hidden')):
            logger.debug("background_slideshow_widget is set to hidden")
            self.setHidden(True)
            return
        self.window = window
        self.logTrace = kamehouseDesktopCfg.getBoolean('background_slideshow_widget', 'trace_log_enabled')
        self.background = ImageWidget("background_slideshow_image_widget", window)
        self.background.lower()
        self.background.lower()
        screen = self.window.app.primaryScreen()
        screenSize = screen.size()
        self.screenWidth = screenSize.width()
        self.screenHeight = screenSize.height()
        self.setBackgroundAnimation()
        self.startBackgroundAnimation()

    def setBackgroundAnimation(self):
        posX = 0
        posY = 0
        imageWidth = self.background.imgSrc.width()
        imageHeight = self.background.imgSrc.height()
        expandPx = kamehouseDesktopCfg.getInt('background_slideshow_widget', 'expand_px')
        expandedPosX = posX - expandPx
        expandedPosY = posY - expandPx
        expandedWidth = self.screenWidth + expandPx * 2 
        expandedHeight = self.screenHeight + expandPx * 2
        animationMs = kamehouseDesktopCfg.getInt('background_slideshow_widget', 'animation_ms')
        minOpacity = kamehouseDesktopCfg.getFloat('background_slideshow_widget', 'min_opacity')
        maxOpacity = kamehouseDesktopCfg.getFloat('background_slideshow_widget', 'max_opacity')
        # contract animation
        self.background.contract = QPropertyAnimation(self.background, b"geometry")
        self.background.contract.setStartValue(QRect(expandedPosX, expandedPosY, expandedWidth, expandedHeight))
        self.background.contract.setEndValue(QRect(posX, posY, self.screenWidth, self.screenHeight))
        self.background.contract.setDuration(animationMs)
        # expand animation
        self.background.expand = QPropertyAnimation(self.background, b"geometry")
        self.background.expand.setStartValue(QRect(posX, posY, self.screenWidth, self.screenHeight))
        self.background.expand.setEndValue(QRect(expandedPosX, expandedPosY, expandedWidth, expandedHeight))
        self.background.expand.setDuration(animationMs)
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
        self.updateBackgroundImage()
        self.reconfigureBackgroundAnimation()
        self.background.animGroup.start()

    def updateBackgroundImage(self):
        self.background.imgSrc = QPixmap("lib/ui/img/banners/dragonball/banner-gohan-ssj2-1.jpg")
        self.background.setPixmap(self.background.imgSrc)

    def reconfigureBackgroundAnimation(self):
        posX = 0
        posY = 0
        imageWidth = self.background.imgSrc.width()
        imageHeight = self.background.imgSrc.height()
        expandPx = kamehouseDesktopCfg.getInt('background_slideshow_widget', 'expand_px')
        expandedPosX = posX - expandPx
        expandedPosY = posY - expandPx
        expandedWidth = self.screenWidth + expandPx * 2 
        expandedHeight = self.screenHeight + expandPx * 2
        # set contract parameters
        self.background.contract.setStartValue(QRect(expandedPosX, expandedPosY, expandedWidth, expandedHeight))
        self.background.contract.setEndValue(QRect(posX, posY, self.screenWidth, self.screenHeight))
        # set expand parameters
        self.background.expand.setStartValue(QRect(posX, posY, self.screenWidth, self.screenHeight))
        self.background.expand.setEndValue(QRect(expandedPosX, expandedPosY, expandedWidth, expandedHeight))
