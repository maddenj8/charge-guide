all_chargers = open("charging-locations.kml" , "r") 

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
                #name =name.replace("amp" , "")
                # trying to uncomment them will add two lines to the end of the output no idea why
                #name =name.replace("amp" , "")
                namelist.append(name)

    charger_file.close()
    return namelist

ccs_names = getname("ccs.kml")
ac43_names = getname("ac43.kml")
chademo_names = getname("chademo.kml")


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
                name = word[8]
                end = (name[len(name) - 3:] )
                if end == "tes":
                        name = name[:19]
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
    text = text[2:]
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


#needs to write to each file a easily read file 
#get ac43 file written 

def setupACFile():
    
    ACoutput = open("ac_output.txt" , "r+") 
    names = getname("ac43.kml")
    latlon = getlatlon("ac43.kml")
    state = getStateAC()
    i = 0 
    while i < len( names ) :
        ACoutput.write( names[ i] + " | "  +  latlon[i] + " |" + state[i] + "\n" )
        i += 1
    ACoutput.close()



def setupCCSFile():
    
    CCSOutput = open("ccs_output.txt" , "r+") 
    names = getname("ccs.kml")
    latlon = getlatlon("ccs.kml")
    state = getStateCcs()

    i = 0 
    while i < len( names ) :
        CCSOutput.write( names[ i] + " | "  +  latlon[i] + " |" + state[i] + "\n" )
        i += 1
    CCSOutput.close()


def setupChademoFile():

    ChademoOutput = open("chademo_output.txt" , "r+") 
    names = getname("chademo.kml")
    latlon = getlatlon("chademo.kml")
    state = getStateChademo()

    i = 0 
    while i < len( names ) :
        ChademoOutput.write( names[ i] + " | "  +  latlon[i] + " |" + state[i] + "\n" )
        i += 1
    ChademoOutput.close()


setupChademoFile()
setupACFile()
setupCCSFile()


