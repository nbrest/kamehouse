import json
import os
import random

from PyQt5.QtCore import Qt, QPropertyAnimation, QParallelAnimationGroup, QSequentialAnimationGroup, QRect, QSize
from PyQt5.QtWidgets import QWidget, QGraphicsOpacityEffect, QLabel
from PyQt5.QtGui import QPixmap, QPalette, QColor, QImageReader, QPainter
from loguru import logger

from config.kamehouse_desktop_cfg import kamehouse_desktop_cfg
from widgets.image import ImageWidget

class BackgroundSlideshowWidget(QWidget):
    window = None
    log_trace = False
    screen_width = 0
    screen_height = 0
    expand_px = 0
    background_images = []
    portrait_background_images = []
    random_image = None
    user_home = None
    default_backgrounds_path = "lib/ui/img/banners"
    backgrounds_success_list_file = "/.kamehouse/data/desktop/backgrounds-success.list"
    backgrounds_error_list_file = "/.kamehouse/data/desktop/backgrounds-error.list"
    
    def __init__(self, window):
        super().__init__(window)
        logger.info("Initializing background_slideshow_widget")
        if (kamehouse_desktop_cfg.getBoolean('background_slideshow_widget', 'hidden')):
            logger.debug("background_slideshow_widget is set to hidden")
            self.setHidden(True)
            return
        self.window = window
        self.log_trace = kamehouse_desktop_cfg.getBoolean('background_slideshow_widget', 'trace_log_enabled')
        self.log_background_images = kamehouse_desktop_cfg.getBoolean('background_slideshow_widget', 'log_background_images')
        self.user_home = os.path.expanduser("~").replace("\\", "/")
        self.expand_px = kamehouse_desktop_cfg.getInt('background_slideshow_widget', 'expand_px')
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
        screen_size = screen.size()
        self.screen_width = screen_size.width()
        self.screen_height = screen_size.height()

    def setBackgroundColor(self):
        self.background_color = QLabel(self.window)
        self.background_color.setStyleSheet(kamehouse_desktop_cfg.get('background_slideshow_widget', 'stylesheet'))
        self.background_color.setGeometry(0, 0, self.screen_width, self.screen_height)

    def setBackgroundPriority(self):
        self.background.lower()
        self.background_color.lower()
        self.background_color.lower()

    def setBackgroundImagesList(self):
        images_src_path = kamehouse_desktop_cfg.get('background_slideshow_widget', 'images_src_path')
        background_images_path = self.user_home + images_src_path
        self.addBackgroundsToLists(background_images_path)
        if (self.log_trace and self.log_background_images):
            background_images_json = json.dumps([obj.__dict__ for obj in self.background_images])
            logger.trace(background_images_json)
            logger.trace("Loaded background images from path: " + background_images_path)
        if (self.useDefaultBackgroundImages()):
            logger.info("Using default background images")
            self.addBackgroundsToLists(self.default_backgrounds_path)

    def useDefaultBackgroundImages(self):
        return len(self.background_images) <= 0

    def addBackgroundsToLists(self, backgrounds_path):
        for root, _, files in os.walk(backgrounds_path):
            for file in files:
                full_path = os.path.join(root, file).replace("\\", "/")
                self.addBackgroundToLists(full_path)

    def addBackgroundToLists(self, full_path):
        if (not self.isValidImageFile(full_path)):
            return
        image = self.getBackgroundImage(full_path)
        if image is None:
            return
        self.background_images.append(image)
        if (image.getPortrait()):
            self.portrait_background_images.append(image)

    def isValidImageFile(self, image_path):
        if (image_path is None):
            return False
        image_path_lower = image_path.lower()
        return (image_path_lower.endswith(".jpeg") or 
                 image_path_lower.endswith(".jpg") or 
                 image_path_lower.endswith(".heic") or 
                 image_path_lower.endswith(".bmp") or 
                 image_path_lower.endswith(".png") or 
                 image_path_lower.endswith(".webp"))

    def getBackgroundImage(self, image_path):
        image = BackgroundImage()
        image.setFilename(image_path)
        image_reader = QImageReader(image_path)
        if not image_reader.canRead():
            logger.error("Can't read image: " + image_path)
            self.updateInvalidBackgroundImageListFile(image_path)
            return None
        size = image_reader.size() 
        if not size.isValid():
            logger.error("Invalid size for image: " + image_path)
            self.updateInvalidBackgroundImageListFile(image_path)
            return None
        width = size.width()
        height = size.height()
        image.setWidth(width)
        image.setHeight(height)
        if (width <= 0 or height <= 0):
            logger.error("Width or height are <= 0 for image: " + image_path)
            self.updateInvalidBackgroundImageListFile(image_path)
            return None
        if (width / height <= 1):
            image.setPortrait(True)
        return image

    def updateInvalidBackgroundImageListFile(self, image_path):
        logger.error("Invalid image: " + image_path)
        self.updateBackgroundImageListFile(self.backgrounds_error_list_file, image_path)
        return

    def setBackgroundAnimation(self):
        animation_ms = kamehouse_desktop_cfg.getInt('background_slideshow_widget', 'animation_ms')
        min_opacity = kamehouse_desktop_cfg.getFloat('background_slideshow_widget', 'min_opacity')
        max_opacity = kamehouse_desktop_cfg.getFloat('background_slideshow_widget', 'max_opacity')
        # contract animation
        self.background.contract = QPropertyAnimation(self.background, b"geometry")
        self.background.contract.setDuration(animation_ms)
        # expand animation
        self.background.expand = QPropertyAnimation(self.background, b"geometry")
        self.background.expand.setDuration(animation_ms)
        self.configureExpandContractParameters()
        # brighten
        effect = QGraphicsOpacityEffect(self.background)
        self.background.setGraphicsEffect(effect)
        self.background.brighten = QPropertyAnimation(effect, b"opacity")
        self.background.brighten.setStartValue(min_opacity)
        self.background.brighten.setEndValue(max_opacity)
        self.background.brighten.setDuration(animation_ms)
        # darken
        self.background.darken = QPropertyAnimation(effect, b"opacity")
        self.background.darken.setStartValue(max_opacity)
        self.background.darken.setEndValue(min_opacity)
        self.background.darken.setDuration(animation_ms)
        # animation groups
        self.background.contract_brighten = QParallelAnimationGroup()
        self.background.contract_brighten.addAnimation(self.background.contract)
        self.background.contract_brighten.addAnimation(self.background.brighten)
        self.background.expand_darken = QParallelAnimationGroup()
        self.background.expand_darken.addAnimation(self.background.expand)
        self.background.expand_darken.addAnimation(self.background.darken)
        self.background.anim_group = QSequentialAnimationGroup()
        self.background.anim_group.addAnimation(self.background.contract_brighten)
        self.background.anim_group.addAnimation(self.background.expand_darken)
        self.background.anim_group.finished.connect(self.restartBackgroundAnimation)

    def configureExpandContractParameters(self):
        pos_x = 0
        pos_y = 0
        expanded_pos_x = 0
        expanded_pos_y = 0
        width = 0
        height = 0
        expanded_width = 0
        expanded_height = 0
        aspect_ratio = 1
        images_width = self.background.img_src.width()
        images_height = self.background.img_src.height()
        if (images_width > 0 and images_height > 0):
            aspect_ratio = images_width / images_height
        else:
            if (self.random_image.getPortrait()):
                logger.error("Invalid image properties generated for combined portrait images")
            else:
                logger.error("Invalid image properties for " + self.random_image.getFilename())
        if (aspect_ratio >= 1):
            width = self.screen_width
            height = self.screen_height
            expanded_pos_x = pos_x - self.expand_px
            expanded_pos_y = pos_y - self.expand_px
            expanded_width = width + self.expand_px * 2 
            expanded_height = height + self.expand_px * 2
        else:
            width = int(self.screen_height * aspect_ratio)
            height = self.screen_height
            pos_x = int((self.screen_width / 2) - (width / 2))
            pos_y = 0
            expanded_pos_x = pos_x - self.expand_px
            expanded_pos_y = pos_y - self.expand_px
            expanded_width = width + self.expand_px * 2 
            expanded_height = height + self.expand_px * 2
        # set contract parameters
        self.background.contract.setStartValue(QRect(expanded_pos_x, expanded_pos_y, expanded_width, expanded_height))
        self.background.contract.setEndValue(QRect(pos_x, pos_y, width, height))
        # set expand parameters
        self.background.expand.setStartValue(QRect(pos_x, pos_y, width, height))
        self.background.expand.setEndValue(QRect(expanded_pos_x, expanded_pos_y, expanded_width, expanded_height))

    def startBackgroundAnimation(self):
        self.background.anim_group.start()

    def restartBackgroundAnimation(self):
        if (self.log_trace):
            logger.trace("Restarting background slideshow animation")
        self.selectRandomBackgroundImage()
        self.configureExpandContractParameters()
        self.background.anim_group.start()

    def selectRandomBackgroundImage(self):
        self.random_image = self.getRandomImage()
        if (self.random_image.getPortrait()):
            self.setPortraitBackground()
        else:
            self.setLandscapeBackground()

    def getRandomImage(self):
        image = random.choice(self.background_images)
        self.logSelectedImage(image)
        return image

    def logSelectedImage(self, image):
        if (self.log_trace and self.log_background_images):
            logger.trace("Loaded image: " + str(image))

    def setPortraitBackground(self):
        portrait_width = int(self.screen_width / 2)
        portrait_height = self.screen_height
        portrait_left = QPixmap(self.random_image.getFilename()).scaled(portrait_width, portrait_height)
        second_portrait_image = self.getSecondPortraitImage()
        portrait_right = QPixmap(second_portrait_image.getFilename()).scaled(portrait_width, portrait_height)
        portrait_separator = kamehouse_desktop_cfg.getInt('background_slideshow_widget', 'portrait_separator_px')
        total_width = portrait_left.width() + portrait_right.width() + portrait_separator
        combined_portraits = QPixmap(total_width, portrait_height)
        combined_portraits.fill(Qt.transparent)
        painter = QPainter(combined_portraits)
        painter.drawPixmap(0, 0, portrait_left)               
        painter.drawPixmap(portrait_left.width() + portrait_separator, 0, portrait_right)
        painter.end()
        self.updateBackgroundImageListFile(self.backgrounds_success_list_file, self.random_image.getFilename())
        self.updateBackgroundImageListFile(self.backgrounds_success_list_file, second_portrait_image.getFilename())
        self.background.img_src = combined_portraits
        self.background.setPixmap(self.background.img_src)

    def getSecondPortraitImage(self):
        filtered_images = [image for image in self.portrait_background_images if (not image.getFilename() == self.random_image.getFilename())]
        image = random.choice(filtered_images)
        self.logSelectedImage(image)
        return image

    def setLandscapeBackground(self):
        pixmap = QPixmap(self.random_image.getFilename())
        self.updateBackgroundImageListFile(self.backgrounds_success_list_file, self.random_image.getFilename())
        self.background.img_src = pixmap
        self.background.setPixmap(self.background.img_src)

    def updateBackgroundImageListFile(self, file_path, current_file_path):
        if (kamehouse_desktop_cfg.getBoolean('background_slideshow_widget', 'skip_update_backgrounds_list_files')):
            return
        backgrounds_file = self.user_home + file_path
        try:
            backgrounds_list = []
            with open(backgrounds_file, 'r') as file:
                backgrounds_list = [line.strip() for line in file]
            if current_file_path not in backgrounds_list:
                with open(backgrounds_file, 'a') as file:
                    file.write(current_file_path + "\n")
        except IOError as error:
            logger.error("Error updating background images list file " + backgrounds_file)

class BackgroundImage():
    filename = None
    is_portrait = False
    width = 0
    height = 0

    def __init__(self):
        return

    def getFilename(self):
        return self.filename

    def setFilename(self, value):
        self.filename = value

    def getPortrait(self):
        return self.is_portrait

    def setPortrait(self, value):
        self.is_portrait = value

    def getWidth(self):
        return self.width

    def setWidth(self, value):
        self.width = value    

    def getHeight(self):
        return self.height

    def setHeight(self, value):
        self.height = value  

    def __str__(self):
        return json.dumps(self.__dict__)