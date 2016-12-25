import json

# store all constants

NULL = "null"

SUCCESS = "success"

FAIL = "fail"

STATUS = "status"

RESULT = "result"

CODE = "code"

MSG = "msg"

UNIT_PRICE = 5

INTERSECT = " INTERSECT "

def default_error_msg(e):
    ret = json.dumps({
                        STATUS: FAIL,
                        RESULT: {
                            CODE: "0",
                            MSG: e
                        }
                    })
    return ret
