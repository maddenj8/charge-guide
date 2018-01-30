all_chargers = open("charging-locations (4).kml" , "r") 
ac43 = open("ac43.xml" , "r+")
chademo = open("chademo.xml" , "r+")
ccs = open("ccs.xml" , "r+" ) 

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
