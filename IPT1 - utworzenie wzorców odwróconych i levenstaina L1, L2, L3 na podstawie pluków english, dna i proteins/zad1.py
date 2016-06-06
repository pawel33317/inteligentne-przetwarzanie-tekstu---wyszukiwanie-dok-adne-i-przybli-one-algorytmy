# -*- coding: cp1250 -*-
import locale,urllib2,re,random,string
locale.setlocale(locale.LC_ALL, '')

def usunZnakiPrzestankowe(text):
    print "Usuwanie znakow przestankowych"
    text = text.lower()
    text = text.replace("."," .")
    text = re.sub(u'[^a-z0-9ąęśćżźłóćń\d\s\.]+', '', text)
    return text
def usunNoweLinie(text):
    print "Usuwanie nowych linii"
    text = re.sub(u'[\t\r\n ]+', ' ', text)
    return text
def zapiszPrzygotowanePliki(text,nazwa):
    print "Zapisuje przygotowany plik: "+nazwa+".prepared"
    f = open(nazwa+".prepared", 'w')
    f.write(text.strip())
    f.close()
def generujWzorzecIwzorzecOdwrocony(dlugoscWzorca, text):
    print "Generuje wzorzec i wzorzec odwrócony"
    separator = "\n"
    wzorzec = ""
    wzorzecRev = ""
    dlugosc_tekstu = len(text)
    ostatni_mozliwy_index_wzorca = dlugosc_tekstu - dlugoscWzorca
    for i in range(0,100):
        losowy_poczatek_wzorca = random.randint(0,ostatni_mozliwy_index_wzorca)
        pojedynczyWzorzec = text[losowy_poczatek_wzorca:losowy_poczatek_wzorca+dlugoscWzorca]
        wzorzec += pojedynczyWzorzec + separator
        wzorzecRev += pojedynczyWzorzec[::-1] + separator
    return (wzorzec, wzorzecRev)
def zapiszWzorce(nazwa, dlugosc, wzorzec, wzorzecRev):
    print "Zapisuje wzorzec: "+nazwa + "_pattern_"+str(dlugosc)
    f = open(nazwa + "_pattern_"+str(dlugosc), 'w')
    f.write(wzorzec.strip())
    f.close()
    print "Zapisuje wzorzec: "+nazwa + "_pattern_rev"+str(dlugosc)
    f = open(nazwa + "_pattern_rev"+str(dlugosc), 'w')
    f.write(wzorzecRev.strip())
    f.close()
def generujZnak():
    zbior = "".join(map(chr, range(97, 123)))+" ."
    return zbior[random.randint(0,len(zbior)-1)] 
def generujWzorzecL(wzorceDokladne):
    wzorzecL = ""
    for wzorzecDokladny in wzorceDokladne.strip().split("\n"):
        wzorzecLPojedynczy = ""
        operation = random.randint(0,2)
        if operation == 0:#wstawienie znaku
            pozycjaDoWstawienia = random.randint(0,len(wzorzecDokladny))
            wzorzecLPojedynczy = wzorzecDokladny[:pozycjaDoWstawienia] + generujZnak() + wzorzecDokladny[pozycjaDoWstawienia:]
        elif operation == 1:#usuniecie znaku
            pozycjaDoUsuniecia = random.randint(0,len(wzorzecDokladny)-1)
            wzorzecLPojedynczy =  wzorzecDokladny[:pozycjaDoUsuniecia] + wzorzecDokladny[(pozycjaDoUsuniecia+1):]
        elif operation == 2:#zamiana znaku
            pozycjaDoZmiany = random.randint(0,len(wzorzecDokladny)-1)
            wzorzecLPojedynczy =  wzorzecDokladny[:pozycjaDoZmiany] + generujZnak() + wzorzecDokladny[(pozycjaDoZmiany+1):]
        wzorzecL += wzorzecLPojedynczy + "\n"
    return wzorzecL
def zapiszWzorceL(nazwa, dlugosc, wzorzecL1, wzorzecL2, wzorzecL3):
    print "Zapisuje wzorzec: "+nazwa + "_pattern_"+str(dlugosc)+"_L1"
    f = open(nazwa + "_pattern_"+str(dlugosc)+"_L1", 'w')
    f.write(wzorzecL1.rstrip('\n'))
    f.close()
    print "Zapisuje wzorzec: "+nazwa + "_pattern_"+str(dlugosc)+"_L2"
    f = open(nazwa + "_pattern_"+str(dlugosc)+"_L2", 'w')
    f.write(wzorzecL2.rstrip('\n'))
    f.close()
    print "Zapisuje wzorzec: "+nazwa + "_pattern_"+str(dlugosc)+"_L3"
    f = open(nazwa + "_pattern_"+str(dlugosc)+"_L3", 'w')
    f.write(wzorzecL3.rstrip('\n'))
    f.close()

fileList = ["english", "proteins", "dna"]
dlugosc_wzorca_M = [4, 8, 16, 32, 1024]

#DLA KAZDEGO PLIKU
for fileName in fileList:
    print "###############################\n###############################\n>>>Przetwarzam plik: "+ fileName
    fileContent = open(fileName+".100MB", 'r').read(500000)
    if fileName == "english":
        print "Wykryto plik english"
        fileContent = usunZnakiPrzestankowe(fileContent)
    fileContent = usunNoweLinie(fileContent)
    zapiszPrzygotowanePliki(fileContent,fileName);

    #DLA KAZDEJ DLUGOSCI WZORCA
    for aktualnaDlugoscWzorca in dlugosc_wzorca_M:
        wzorzec, wzorzecRev = generujWzorzecIwzorzecOdwrocony(aktualnaDlugoscWzorca,fileContent)
        zapiszWzorce(fileName, aktualnaDlugoscWzorca, wzorzec, wzorzecRev)
        print "Generuje wzorce L1, L2, L3"
        wzorzecL1 = generujWzorzecL(wzorzec)
        wzorzecL2 = generujWzorzecL(wzorzecL1)
        wzorzecL3 = generujWzorzecL(wzorzecL2)
        zapiszWzorceL(fileName, aktualnaDlugoscWzorca, wzorzecL1, wzorzecL2, wzorzecL3)
        







