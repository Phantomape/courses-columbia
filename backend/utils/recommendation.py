import httplib, urllib, base64

headers = {
    'Ocp-Apim-Subscription-Key': 'cd2c30852f5f4e0fafc749df01ee4ef5',
}

paramsI = urllib.urlencode({
    # Request parameters
    'includeMetadata': '{boolean}',
    'buildId': '{integer}',
})

paramsU = urllib.urlencode({
    # Request parameters
    'itemsIds': '{string}',
    'includeMetadata': '{boolean}',
    'buildId': '{integer}',
})

def getItem2ItemRecommendation(item_id, numberOfResults, minScore):
    try:
        conn = httplib.HTTPSConnection('westus.api.cognitive.microsoft.com')
        conn.request("GET", "/recommendations/v4.0/models/d24f6ee7-4c2b-4e92-9b49-fad2f8651d8e/recommend/item?itemIds="+ str(item_id) + "&numberOfResults=" + str(numberOfResults)+ "&minimalScore=" + str(minScore) + "&%s" % paramsI, "{body}", headers)
        response = conn.getresponse()
        data = response.read()
        print(data)
        conn.close()
        return data
    except Exception as e:
        print("[Errno {0}] {1}".format(e.errno, e.strerror))
        return "Error!"

def getUser2ItemRecommendation(user_id, numberOfResults):
    try:
        conn = httplib.HTTPSConnection('westus.api.cognitive.microsoft.com')
        conn.request("GET", "/recommendations/v4.0/models/d24f6ee7-4c2b-4e92-9b49-fad2f8651d8e/recommend/user?userId="+ str(user_id) + "&numberOfResults=" + str(numberOfResults)+ "&%s" % paramsU, "{body}", headers)
        response = conn.getresponse()
        data = response.read()
        conn.close()
        return data
    except Exception as e:
        print("[Errno {0}] {1}".format(e.errno, e.strerror))
        return "Error"

if __name__ == '__main__':
    #res = getItem2ItemRecommendation("UsFtqoBl7naz8AVUBZMjQQ", 1, 50)
    res = getUser2ItemRecommendation("Iu6AxdBYGR4A0wspR9BYHA", 1)
    print res