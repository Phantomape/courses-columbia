from utils.crossdomain import *
import json
from . import routes
from utils.connect_db import *
from utils.DaoLayer import *


# Register a new user.
# http://127.0.0.1:8080/api/users/register
@routes.route('/api/users/register', methods=['GET', 'POST'])
@crossdomain(origin='*')
def user_register():
    if request.method == 'POST':
        try:
            username = request.form['username']
            email = request.form['email']
            password = request.form['password']
            print username
            print email
            print password
            data = {
                "password": password,
                "uid": email,
                "username": username,
                "email": email
            }
            user_profile = getByEmail(email)
            if user_profile != None:
                ret = {"status": False}
            else:
                # user_profile = {
                #     "uid": "123",
                #     "username": "liuhaikuo",
                #     "password": "pwd",
                #     "tag": ["food catagory1", "food catagory2"],
                #     "search_history": ["keyword1", "keyword2"]
                # }
                insertData(data)
                ret = {"status": True}
                data.pop("_id")
                ret["user_profile"] = data
            print ret
            return json.dumps(ret)

        except Exception, e:
            print e
            ret = {"status": False}
            return json.dumps(ret)


# User login
# http://127.0.0.1:8080/api/users/login
@routes.route('/api/users/login', methods=['GET', 'POST'])
@crossdomain(origin='*')
def user_login():
    if request.method == 'POST':
        try:
            email = request.form['email']
            password = request.form['password']
            print email
            print password
            user_profile = getByEmail(email)
#             print user_profile["password"]
            if user_profile == None or user_profile["password"] != password:
                ret = {"status": False}
            else:
                user_profile.pop("_id")
                # user_profile = {
                #     "uid": "123",
                #     "username": "liuhaikuo",
                #     "password": "pwd",
                #     "tag": ["food catagory1", "food catagory2"],
                #     "search_history": ["keyword1", "keyword2"]
                # }
                ret = {"status": True}
                ret["tag"] = []
                ret["search_history"] = []
                tmp = user_profile
                user_profile = {
                    "uid": tmp["user_id"],
                    "username": tmp["name"],
                    "password": tmp["password"],
                }
                ret["user_profile"] = user_profile
            print ret
            return json.dumps(ret)
        except Exception, e:
            print e
            ret = {"status": False}
            return json.dumps(ret)
