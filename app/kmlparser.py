all_chargers = open("charging-locations (4).kml" , "r") 
ac43 = open("ac43.kml" , "r+")
chademo = open("chademo.kml" , "r+")
ccs = open("ccs.kml" , "r+" ) 

acpart = False
ccspart = False
chademopart = False
i = 0 
a = ""
text= all_chargers.readlines()
for line in text: 


    if  "<!-- ======== AC43 ======= -->" in line :
        acpart= True
   
    if "<!-- ======== ComboCCS ======= -->" in line:
        ccspart = True
        acpart = False
        chademopart = False

    if "<!-- ======== CHAdeMO ======= -->" in line :
        chademopart = True
        ccpart = False
        acpart = False
        

    if acpart == True:
        ac43.write(line)         
        
    
    if ccspart == True:
        ccs.write(line)         
       
    if chademopart == True:
        chademo.write(line)         

ac43.close()


def getname(file_name ):

    namelist = []
    name = ""
    charger_file = open( file_name , "r")

    text = charger_file.readlines()

    text = text[1:]
    for line in text:
        word = line.split(">")
        if len(word) >=9:
            name = word[2]
            name = name[:-6]
            if len(name) != 0:

                namelist.append(name)

    charger_file.close()

    return namelist


a = getname("ac43.kml")

print(a)
