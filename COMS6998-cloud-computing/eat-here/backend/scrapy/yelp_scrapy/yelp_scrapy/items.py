import scrapy


class Review(scrapy.Item):
    filepath = scrapy.Field()
    userId = scrapy.Field()
    userName = scrapy.Field()
    rating = scrapy.Field()
    business = scrapy.Field()
    review = scrapy.Field()

