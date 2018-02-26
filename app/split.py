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
        ac43.write(line )
        acpart = True




    if "<!-- ======== CHAdeMO ======= -->" in line :
        chademo.write(line)
        chademopart = True
        ccspart = False
        acpart = False

    if "<!-- ======== ComboCCS ======= -->" in line:
        ac43.close()
        ccs.write(line)
        ccspart = True
        acpart = False


    if acpart == True:
        ac43.write(line)

    elif ccspart == True :
        chademopart = False
        ccs.write(line)
        if  "<!-- ======== CHAdeMO ======= -->" in line :
            ccspart = False
            ccs.close()


    elif  chademopart == True:
        ccspart = False
        chademo.write(line)


    if line == "</kml>":
        chademo.close()