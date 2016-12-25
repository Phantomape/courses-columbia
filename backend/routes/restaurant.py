from utils.crossdomain import *
import json
from . import routes
from utils.recommendation import *
from utils.DaoLayer import *
import math

# http://0.0.0.0:8080/api/restaurant/recommend/
@routes.route('/api/restaurant/recommend', methods=['GET', 'POST'])
@crossdomain(origin='*')
def recommend():
    try:
        lat = float(request.form['latitude'])
        lng = float(request.form['longitude'])
        uid = request.form['uid']
        if uid == "rpOyqD_893cqmDAtJLbdog":
            rows = search_dao("pizza")
            restaurant_infos = []
            ret = {"status": True}
            for tmp in rows:
                restaurant_info = {
                    "rid": tmp["item_id"],
                    "name": tmp["item_name"],
                    "address": tmp["address"],
                    "latitude": tmp["latitude"],
                    "longitude": tmp["longitude"],
                    "picUrl": tmp["picUrl"],
                    "overallRating": tmp["overallRating"],
                }
                restaurant_infos.append(restaurant_info)
                restaurant_info["distance"] = math.sqrt(
                    pow(restaurant_info["latitude"] - lat, 2) + pow(restaurant_info["longitude"] - lng, 2))
            def age(s):
                return s['distance']
            restaurant_infos = sorted(restaurant_infos, key=age)
            ret["restaurant_infos"] = restaurant_infos[0:5]
            print ret
            return json.dumps(ret)

        print lat
        print lng
        # print uid
        ret = {"status": True}
        if uid == "empty_uid":
            ret["restaurant_infos"] = []
            return json.dumps(ret)
        # restaurant_infos = [
        #     {
        #         "rid": "reyes-deli-and-grocery-brooklyn",
        #         "name": "Reyes Deli & Grocery",
        #         "address": "addr A",
        #         "latitude": 40.75033,
        #         "longitude": -73.98531,
        #         "picUrl": "https://www.omnihotels.com/-/media/images/hotels/homrst/restaurants/homrst-omni-homestead-resort-jeffersons-restaurant-2.jpg",
        #         "overallRating": 4.5
        #     },
        #     {
        #         "rid": "coffee-project-new-york-new-york",
        #         "name": "Coffee Project New York",
        #         "address": "local B",
        #         "latitude": 40.7274823,
        #         "longitude": -73.9902387,
        #         "picUrl": "http://assets.sheratonseattle.com/lps/assets/u/she460re-119596-Daily-Grill-Restaurant.jpg",
        #         "overallRating": 5.0
        #     }
        # ]
        items = getUser2ItemRecommendation(uid, 4)
        items = json.loads(items)["recommendedItems"]
        # print items
        restaurant_infos = []
        for item in items:
            # print item
            if item is None:
                continue
            print item["items"][0]["id"]
            tmp = getByRestaurant_id(item["items"][0]["id"])
            # print "~~~~~~~~~~~"
            # print tmp
            # print "~~~~~~~~~~~"
            restaurant_info = {
                "rid": tmp["item_id"],
                "name": tmp["item_name"],
                "address": tmp["address"],
                "latitude": tmp["latitude"],
                "longitude": tmp["longitude"],
                "picUrl": tmp["picUrl"],
                "overallRating": tmp["overallRating"],
                # "catagory": tmp["catagory"]
            }
            restaurant_info["distance"] = math.sqrt(pow(restaurant_info["latitude"] - lat, 2) + pow(restaurant_info["longitude"] - lng,2))
            restaurant_infos.append(restaurant_info)
            # restaurant_info

        def age(s):
            return s['distance']
        restaurant_infos = sorted(restaurant_infos, key=age)
        ret["restaurant_infos"] = restaurant_infos
        print ret
        return json.dumps(ret)
    except Exception, e:
        print e
        ret = {"status": False}
        return json.dumps(ret)


@routes.route('/api/restaurant/search', methods=['GET', 'POST'])
def search():
    if request.method == 'POST':
        try:
            keyword = request.form['keyword']
            lat = float(request.form['latitude'])
            lng = float(request.form['longitude'])
            # uid = request.form['uid']
            # print uid
            print keyword
            # recommend logic
            ret = {"status": True}
            # restaurant_infos = [
            #     {
            #         "rid": "reyes-deli-and-grocery-brooklyn",
            #         "name": "Reyes Deli & Grocery",
            #         "address": "addr A",
            #         "latitude": 40.75033,
            #         "longitude": -73.98531,
            #         "picUrl": "https://www.omnihotels.com/-/media/images/hotels/homrst/restaurants/homrst-omni-homestead-resort-jeffersons-restaurant-2.jpg",
            #         "overallRating": 4.5
            #     },
            #     {
            #         "rid": "coffee-project-new-york-new-york",
            #         "name": "Coffee Project New York",
            #         "address": "local B",
            #         "latitude": 40.7274823,
            #         "longitude": -73.9902387,
            #         "picUrl": "http://assets.sheratonseattle.com/lps/assets/u/she460re-119596-Daily-Grill-Restaurant.jpg",
            #         "overallRating": 5.0
            #     }
            # ]
            restaurant_infos = []
            rows = search_dao(keyword)
            for tmp in rows:
                restaurant_info = {
                    "rid": tmp["item_id"],
                    "name": tmp["item_name"],
                    "address": tmp["address"],
                    "latitude": tmp["latitude"],
                    "longitude": tmp["longitude"],
                    "picUrl": tmp["picUrl"],
                    "overallRating": tmp["overallRating"],
                    # "catagory": tmp["catagory"]
                }
                restaurant_infos.append(restaurant_info)
                restaurant_info["distance"] = math.sqrt(pow(restaurant_info["latitude"] - lat, 2) + pow(restaurant_info["longitude"] - lng, 2))
            def age(s):
                return s['distance']
            restaurant_infos = sorted(restaurant_infos, key=age)
            ret["restaurant_infos"] = restaurant_infos[0:5]
            print ret
            return json.dumps(ret)
        except Exception, e:
            print e
            ret = {"status": False}
            return json.dumps(ret)


# @routes.route('/api/restaurant/recommend_realtime', methods=['GET', 'POST'])
# @crossdomain(origin='*')
# def recommend_realtime():
#     try:
#         if request.method == 'POST':
#             lat = request.form['latitude']
#             lng = request.form['longitude']
#             uid = request.form['uid']
#             print lat
#             print lng
#             print uid
#             # db = get_db_connection()
#             # restaurant_collection = db.restaurant
#             # query_results = restaurant_collection.find_one({"rid": rid})
#             # get several rids here by recommend algorithm.
#
#             rids = [1, 2, 3]
#             # for rid in rids:
#             #     # search in db and get detailed info of restaurant
#             #     restaurant_info = {}
#             #     restaurant_infos.append(restaurant_info)
#             ret = {"status": True}
#             if uid == "empty_uid":
#                 ret["restaurant_info"] = {}
#                 return json.dumps(ret)
#             restaurant_info = {
#                 "rid": "coffee-project-new-york-new-york",
#                 "name": "Coffee Project New York",
#                 "address": "local B",
#                 "latitude": 40.7274823,
#                 "longitude": -73.9902387,
#                 "picUrl": "http://assets.sheratonseattle.com/lps/assets/u/she460re-119596-Daily-Grill-Restaurant.jpg",
#                 "overallRating": 5.0
#             }
#             ret["restaurant_info"] = restaurant_info
#             print ret
#             return json.dumps(ret)
#     except Exception, e:
#         print e
#         ret = {"status": False}
#         return json.dumps(ret)
