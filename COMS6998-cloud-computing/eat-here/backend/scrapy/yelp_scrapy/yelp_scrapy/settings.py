BOT_NAME = 'yelp_scrapy'

SPIDER_MODULES = ['yelp_scrapy.spiders']
NEWSPIDER_MODULE = 'yelp_scrapy.spiders'
LOG_LEVEL = 'INFO'
ROBOTSTXT_OBEY = False
DOWNLOAD_DELAY = 0.5
ITEM_PIPELINES = {
   'yelp_scrapy.pipelines.ReviewPipeline': 300,
}
