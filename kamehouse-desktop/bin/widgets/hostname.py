import socket

from widgets.text import TextWidget

class HostnameWidget(TextWidget):
    def __init__(self, window):
        super().__init__('hostname_widget', socket.gethostname(), window)
