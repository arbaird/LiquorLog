from google.appengine.ext import ndb
import json
from random import shuffle


class Ingredient(ndb.Model):
    qty = ndb.StringProperty()
    measure = ndb.StringProperty()
    name = ndb.StringProperty()



class LiquorStorage(ndb.Model):
    name = ndb.StringProperty()

    ingredients = ndb.JsonProperty()
    msg = ndb.TextProperty()
    date_created = ndb.DateTimeProperty(auto_now_add=True)
    userCreated = ndb.BooleanProperty()
    imgId = ndb.IntegerProperty()
    downloadCount = ndb.IntegerProperty()
    ingredientsObj = ndb.StructuredProperty(Ingredient, repeated=True)
    uniqueid = ndb.StringProperty()

    name_lower = ndb.StringProperty(repeated=True)
    ingredients_searchable_names = ndb.StringProperty(repeated=True)


    version = ndb.IntegerProperty()

#naive, simple password implementation to only clear if pw=imsure is passed as a var. Not very sophisticated,
#but prevents accidental clears and adds at least some protection against unwanted clearing. Mostly used
#for cleaning database after testing out edit drink activity multiple times on nonsense drinks
def clear():
    pw = request.vars.pw
    if pw != "imsure":
        return response.json("bad")
    ndb.delete_multi(LiquorStorage.query().fetch(keys_only=True))

    return response.json("cleared")

def clear_user_recipes():
    pw = request.vars.pw
    if pw != "imsure":
        return response.json("bad")
    userDrinks = LiquorStorage.query(LiquorStorage.userCreated == True)
    for drink in userDrinks:
        drink.key.delete()

    return response.json("cleared")

def clear_lib_recipes():
    pw = request.vars.pw
    if pw != "imsure":
        return response.json("bad")

    libDrinks = LiquorStorage.query(LiquorStorage.userCreated == False)
    for drink in libDrinks:
        drink.key.delete()
    return response.json("cleared")

#used to add recipes with a GET. Only used for localhost for debugging so that errors can be printed somewhere,
#for some reason error tickets are not displaying with web2py
def sadd_recipe_fancy():
    name = request.vars.name
    if name == "":
        return response.json("bad")
    msg = request.vars.msg
    print(request.vars.name, request.vars.msg, request.vars.ingredients)
    ingredients = json.loads(request.vars.ingredients)
    imgId = request.vars.imgId


    ls = LiquorStorage()


    ingredients_searchable_names = []
    for ingredient in ingredients:

        ls.ingredientsObj.append(Ingredient(qty=ingredient["qty"].encode("utf-8"),
                                            name=ingredient["name"],
                                            measure=ingredient["measure"],

                                         ))
        for word in ingredient["name"].split(" "):
            ingredients_searchable_names.append(word.lower())
        #if len(ingredient["name"].split(" ")) > 1:
            #ingredients_searchable_names.append(ingredient["name"].lower())


    print(ingredients_searchable_names)



    ls.userCreated = True
    ls.name = name
    ls.name_lower = [word.lower() for word in name.split(" ")]
    ls.name_lower.append(name.lower())
    ls.ingredients_searchable_names = ingredients_searchable_names
    ls.msg = msg
    ls.version = 1
    ls.ingredients = ingredients
    ls.imgId = imgId
    ls.downloadCount = 0
    ls.uniqueid = str(ls.put().id())



    ls.put()

    return response.json("okayyyydokayyy")

#increment the download count of a drink given its uniqueId to track popularity
def increment_download_count():
    drinkId = request.post_vars.drinkId

    q = LiquorStorage.query(LiquorStorage.uniqueid == drinkId).fetch(1)
    for r in q:
        r.downloadCount += 1
        r.put()
    return response.json(dict(results="ok"))

#returns a list of drink recipes given a query
def get_result_list(qry):
    res = []
    for r in qry:
        res.append(dict(
            name=r.name,
            msg=r.msg,
            date_created=r.date_created,
            ingredients=r.ingredients,
            imgId=r.imgId,
            userCreated=r.userCreated,
            uniqueid=r.uniqueid,
            downloads=r.downloadCount,
            ingredients_searchable_names = r.ingredients_searchable_names
        ))
    return response.json(dict(results=res))

#returns 15 most downloaded drinks in descending order
def get_popular_drinks():

    qry = LiquorStorage.query(LiquorStorage.downloadCount > 0).order(-LiquorStorage.downloadCount).fetch(15)

    return get_result_list(qry)

#add a recipe to the database
def add_recipe_fancy():
    name = request.post_vars.name
    if name == "":
        return response.json("bad")
    msg = request.post_vars.msg
    ingredients = json.loads(request.post_vars.ingredients)
    imgId = request.post_vars.imgId


    ls = LiquorStorage()
    ingredients_searchable_names = []
    for ingredient in ingredients:
        ls.ingredientsObj.append(Ingredient(qty=ingredient["qty"].encode("utf-8"),
                                            name=ingredient["name"],
                                            measure=ingredient["measure"]))
        for word in ingredient["name"].split(" "):
            ingredients_searchable_names.append(word.lower())
        if len(ingredient["name"].split(" ")) > 1:
            ingredients_searchable_names.append(ingredient["name"].lower())




    ls.userCreated = request.vars.userCreated
    ls.name = name
    ls.name_lower = [word.lower() for word in name.split(" ")]
    ls.name_lower.append(name.lower())
    ls.ingredients_searchable_names = ingredients_searchable_names
    ls.msg = msg
    ls.version = 1
    ls.ingredients = ingredients
    ls.imgId = imgId
    ls.downloadCount = 0
    ls.uniqueid = str(ls.put().id())
    ls.put()

    return response.json("okayyyydokayyy")




#returns 15 random user recipes. This way, old and new drinks have equal chance of showing up in library
#and old drinks will not be burried
def get_user_recipes():
    qry = LiquorStorage.query(LiquorStorage.userCreated == True).fetch()
    print(qry)
    shuffle(qry)
    print(qry)

    qry = qry[:16]


    return get_result_list(qry)

#returns predefined, non user made drinks
def get_library_recipes():
    qry = LiquorStorage.query(LiquorStorage.userCreated == False).fetch(15)

    return get_result_list(qry)


#searches for drinks guven a query string based on drink name and ingredient names
def search():

    q = request.vars.q.strip().lower()

    limit = q[:-1] + chr(ord(q[-1]) + 1)
    qry = LiquorStorage.query(ndb.OR(
        ndb.AND(LiquorStorage.name_lower >= q, LiquorStorage.name_lower < limit),
        ndb.AND(LiquorStorage.ingredients_searchable_names >= q, LiquorStorage.ingredients_searchable_names < limit)
                              ))

    return get_result_list(qry)

"""
Everything below is used for reading predefined drinks from a text file in the root
of web2py_backend_tests. This is included in this file so everythin is added to same LiquorStorage ndb.model
"""
#adds predefined drinks from the .txt file in the root directory as non user created drinks.
#adapted form java code, so the syntax may appear a bit strange for python
def addPreDefined():


    f = open("Drinks.txt", 'rb')
    file_contents = f.read().decode("utf-8")


    drinks = []
    drinksAsStr = ""

    for line in file_contents.split("\n"):


        if line != "#":
            drinksAsStr += line.encode("utf-8") + "\n"
        else:
            drinks.append(drinksAsStr)
            drinksAsStr = ""

    img_ids = get_ImID_array()




    for drink_str in drinks:
        split_drink = [line for line in drink_str.split("\n")]
        drink_name = split_drink[0]
        i = 1
        ings = split_drink[i]

        ls = LiquorStorage()

        ingredients_searchable_names = []
        while ings != "-":
            ingInfo = parse_ings(ings)
            ls.ingredientsObj.append(Ingredient(qty=ingInfo["qty"],
                                            name=ingInfo["ingredient"],
                                            measure=ingInfo["measure"]))
            for word in ingInfo["ingredient"].split(" "):
                ingredients_searchable_names.append(word.lower())
            if len(ingInfo["ingredient"].split(" ")) > 1:
                ingredients_searchable_names.append(ingInfo["ingredient"].lower())
            i +=1
            ings = split_drink[i]
        i += 1
        imgIdPos = int(split_drink[i])
        imgId = img_ids[imgIdPos]
        i +=1
        msg = split_drink[i]


        ls.userCreated = False
        ls.name = drink_name
        ls.name_lower = [word.lower() for word in drink_name.split(" ")]
        ls.name_lower.append(drink_name.lower())
        ls.ingredients_searchable_names = ingredients_searchable_names
        ls.msg = msg
        ls.version = 1
        ls.ingredients = ingredientToJSON(ls.ingredientsObj)
        ls.imgId = imgId
        ls.downloadCount = 0
        ls.uniqueid = str(ls.put().id())
        ls.put()

##parse an ingredient line of the predefined drinks text file. return a dict for qty, name, and measure
#adapted from java code, which is why some python syntax seems a bit strange!
def parse_ings(line):

    lineInfo = dict()
    tokens = line.split(" ")
    if tokens[0] == "/" : #this means no qty or measure is specified

        lineInfo["qty"] =  ""
        lineInfo["measure"] =  ""
        name = ""
        for i in range(1, len(tokens)):
            name += tokens[i] + " "

        name = name.strip()
        lineInfo["ingredient"] =  name

    elif tokens[1] == "/": #this means no measure is specified

        lineInfo["qty"] =  tokens[0]
        lineInfo["measure"] =  ""
        name = ""
        for i in range(2, len(tokens)):
            name += tokens[i] + " "
        name = name.strip();
        lineInfo["ingredient"]  = name
    else:                               #else, qty and measure are specified
        lineInfo["qty"] = tokens[0]
        lineInfo["measure"] =  tokens[1]
        name = ""
        for i in range(2, len(tokens)):
            name += tokens[i] + " ";
        name = name.strip();
        lineInfo["ingredient"] =  name

    return lineInfo;

#converts Ingredient object to array, useful for adding predefined drinks from txt file
def ingredientToJSON(ingredientsObj):
    return [ingredient.to_dict() for ingredient in ingredientsObj]




#return an array of the imgIds for each drinks, based on .txt file in root directory
def get_ImID_array():
    f = open("drinkImageIds.txt", 'rb')
    file_contents = f.read().decode("utf-8")



    imgIds = []


    for line in file_contents.split("\n"):
        imgId = int(line.split(" ")[-1])
        imgIds.append(imgId)
    return imgIds
