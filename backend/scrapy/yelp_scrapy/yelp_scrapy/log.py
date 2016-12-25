import logging

logger = logging.getLogger('my-logger')
logger.setLevel(logging.INFO)

err_handler = logging.FileHandler('error_log.txt', 'a')
err_handler.setLevel(logging.WARNING)

info_handler = logging.FileHandler('info_log.txt', 'a')
info_handler.setLevel(logging.INFO)

logger.addHandler(err_handler)
logger.addHandler(info_handler)
