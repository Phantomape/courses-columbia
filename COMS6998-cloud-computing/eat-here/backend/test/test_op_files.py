import json


f = open('user.jl', 'r')
f1 = open('target.jl', 'w')
for l in f:
    j = json.loads(l)[0]
    print j
    s = json.dumps(j)
    f1.write(s + "\n")



