all_chargers = open("charging-locations.kml" , "r") 
ac43 = open("ac43.kml" , "r+")
chademo = open("chademo.kml" , "r+")
ccs = open("ccs.kml" , "r+" ) 

acpart = False
ccspart = False
chademopart = False
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
        ccspart = False
        acpart = False
        
    if ccspart == True:
        ccs.write(line)         
       

    elif acpart == True:
        ac43.write(line)         
        
    
    elif chademopart == True:
        chademo.write(line)         

ac43.close()
ccs.close()
chademo.close()

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



def getlatlon( file_name):

    namelist = []
    name = ""
    charger_file = open( file_name , "r")

    text = charger_file.readlines()

    text = text[1:]
    for line in text:
        word = line.split(">")
        if len(word) >=9:
            name = word[2]
            if len(word) > 9:
               # print(word[8]  )
                name = word[8]
                end = (name[len(name) - 3:] )
                if end == "tes":
                        namelist.append(name)
                
    
    charger_file.close()
    return  namelist

def getStateAC( ):

    namelist = []
    name = ""
    charger_file = open( "ac43.kml" , "r")

    text = charger_file.readlines()
    nextlineget = False
    text = text[1:]
    for line in text:
        if nextlineget == True:
            line = line.split("<")
            state = line[0]
            state = state.split(" " ) 
            state.pop()
            line = ' '.join(state)
            namelist.append(line)
            nextlineget = False

        if line[:3] == "<Pl":
            nextlineget = True 
    
    return namelist


def getStateChademo():


    namelist = []
    name = ""
    charger_file =open( "chademo.kml" , "r")

    text = charger_file.readlines()
    nextlineget = False
    text = text[1:]
    for line in text:
        if nextlineget == True:
            line = line.split("<")
            state = line[0]
            state = state.strip(",")
            state = state.strip(" ")
            state = state.split(" " ) 
            if len (state) >4:
                state.pop()
            line = ' '.join(state)
            namelist.append(line)
            nextlineget = False

        if line[:3] == "<Pl":
            nextlineget = True 
    
    return namelist




def getStateCcs():
    namelist = []
    name = ""
    charger_file =open( "ccs.kml" , "r")

    text = charger_file.readlines()
    nextlineget = False
    text = text[1:]
    for line in text:
        if nextlineget == True:
            line = line.split("<")
            state = line[0]
            state = state.strip(",")
            state = state.strip(" ")
            state = state.split(" " ) 
            if len (state) >4:
                state.pop()
            line = ' '.join(state)
            namelist.append(line)
            nextlineget = False

        if line[:3] == "<Pl":
            nextlineget = True 
    
    return namelist

