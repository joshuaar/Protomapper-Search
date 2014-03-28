package com.protomapper.test
import org.scalatest.FunSuite
import java.io.File
import com.protomapper.compile._
import com.protomapper.update._
import com.protomapper.search._
import org.biojava3.core.sequence.ProteinSequence
import org.apache.lucene.store.NIOFSDirectory
import sys.process._

object SearchTestGlobals {
    def getAccess():LuceneAccess = {
	  val ix = new NIOFSDirectory(TestGlobals.ixPath)
	  new LuceneAccess(ix)      
    }
    val pathogenQS = "(Leishmania AND donovani) OR (Mycobacterium AND ulcerans) OR (Peptostreptococcus) OR (Mycoplasma AND arthritidis) OR (herpesvirus) OR (Norovirus) OR (Candida AND albicans) OR (Schistosoma AND intercalatum) OR (Rickettsia AND africae) OR (Salmonella AND typhimurium) OR (Schistosoma AND japonicum) OR (Vibrio AND cholerae) OR (Dialister AND pneumosintes) OR (Burkholderia AND pseudomallei) OR (Escherichia AND coli) OR (Prevotella AND intermedia) OR (Bartonella AND rochalimae) OR (Bartonella AND clarridgeiae) OR (Bacillus AND anthracis) OR (Tropheryma AND whipplei) OR (Echinococcus AND multilocularis) OR (Erythema AND infectiosum) OR (Trypanosoma AND brucei) OR (Neisseria AND meningitidis) OR (Rickettsia AND sibirica) OR (Brugia AND malayi) OR (Yersinia AND pestis) OR (Chlamydia AND trachomatis) OR (Clostridium AND perfringens) OR (Hepatitis AND E AND virus) OR (Taenia AND asiatica) OR (Leishmania AND amazonensis) OR (Plasmodium AND falciparum) OR (Blastomyces AND dermatitidis) OR (Human AND herpesvirus AND 4) OR (human AND herpesvirus) OR (Aspergillus AND flavus) OR (Schistosoma AND mekongi) OR (Angiostrongylus AND costaricensis) OR (Aspergillus AND niger) OR (Bacteroides AND vulgatus) OR (Francisella AND tularensis) OR (Dengue AND virus) OR (Bacteroides AND fragilis) OR (Moraxella AND catarrhalis) OR (Yersinia AND pestis) OR (Rickettsia AND conorii) OR (Bartonella AND elizabethae) OR (Borrelia AND garinii) OR (Human AND papillomavirus) OR (Plasmodium AND vivax) OR (Leptospira AND canicola) OR (Ureaplasma AND urealyticum) OR (Hepatitis AND D AND virus) OR (Bartonella AND grahamii) OR (Burkolderia AND mallei) OR (parainfluenza AND virus) OR (Acinetobacter AND baumannii) OR (Borrelia AND recurrentis) OR (Anaplasma AND phagocytophilia) OR (Haemophilus AND influenza) OR (Influenza AND B AND virus) OR (Rickettsia AND canadensis) OR (Mycobacterium AND paratuberculosis) OR (Porphyromonas AND gingivalis) OR (Borrelia AND burgdorferi) OR (Mycoplasma AND hominis) OR (Salmonella AND enterica) OR (Rickettsia AND felis) OR (Respiratory AND syncytial AND virus) OR (Rickettsia AND bellii) OR (Rift AND Valley AND fever AND virus) OR (Klebsiella AND pneumoniae) OR (Acinetobacter) OR (La AND Crosse AND virus) OR (Mycoplasma AND penetrans) OR (Yellow AND fever AND virus) OR (Campylobacter AND jejuni) OR (Chlamydophila) OR (Haemophilus AND influenzae AND (type AND B)) OR (Corynebacterium AND haemolyticus) OR (Mycobacterium AND leprae) OR (Clostridium AND species) OR (Ehrlichia AND ewingii) OR (Measles AND virus) OR (Babesia AND sp. AND EU1) OR (Wuchereria AND bancrofti) OR (Lassa AND virus) OR (rhinovirus) OR (Borrelia AND spielmanii) OR (Aspergillus AND fumigatus) OR (Haemophilus AND influenzae) OR (Rickettsia AND japonica) OR (Plasmodium AND ovale) OR (Trichophyton AND mentagrophytes) OR (Babesia AND divergens) OR (Human AND endogenous AND retrovirus) OR (Nocardia AND asteroides) OR (Anaplasma AND phagocytophilum) OR (Bacteroides AND forsythus) OR (Coxiella AND burnetti) OR (Trichophyton AND interdigitale) OR (Human AND endogenous AND retrovirus AND type AND W) OR (Human AND papillomavirus) OR (Rickettsia AND akari) OR (Burkholderia AND multivorans) OR (Influenza AND C AND virus) OR (Chlamydophila AND pneumoniae) OR (Clostridium AND botulinum) OR (Mumps AND virus) OR (Bocavirus) OR (Bordetella AND pertussis) OR (Astrovirus) OR (Rhizopus AND species) OR (Branhamella AND catarrhalis) OR (Novosphingobium AND aromaticivorans) OR (Vibrio AND parahaemolyticus) OR (Mycoplasma AND fermentans) OR (Actinobacillus AND actinomycetemcomitans) OR (Herpesviruses) OR (Brucella AND canis) OR (Simian AND virus AND 40) OR (Peptostreptococcus AND micros) OR (Rhizopus AND oryzae) OR (Leishmania AND braziliensis) OR (Enterococcus) OR (Gardnerella AND vaginalis) OR (Neisseria AND gonorrhoeae) OR (Human AND foamy AND virus) OR (Schistosoma AND mansoni) OR (Cryptococcus AND gattii) OR (Legionella AND pneumophila) OR (Human AND papillomavirus AND 11) OR (Schistosoma AND hematobium) OR (Human AND herpesvirus AND 7) OR (Neorickettsia AND sennetsu) OR (Capnocytophaga AND ochracea) OR (Plasmodium AND malariae) OR (Powassan AND virus) OR (Mycobacterium AND tuberculosis) OR (Human AND parvovirus) OR (Burkholderia AND cenocepacia) OR (Corynebacterium AND diphtheriae) OR (Chlamydophila AND psittaci) OR (Rickettsia AND parkeri) OR (Fusobacterium AND nucleatum) OR (Leishmania AND tropica) OR (Mycoplasma AND pneumoniae) OR (Rubella AND virus) OR (Helicobacter) OR (Paragonimus AND westermani) OR (Listeria AND monocytogenes) OR (Borrelia AND parkeri) OR (Leishmania AND chagasi) OR (Echovirus AND 7) OR (Human AND T-cell AND lymphotropic AND virus AND type AND 2) OR (Clostridium AND difficile) OR (Shigella AND flexneri) OR (Cryptococcus AND neoformans) OR (Metapneumovirus) OR (Bartonella AND henselae) OR (Bordetella AND parapertussis) OR (Leishmania AND major) OR (Human AND T-cell AND lymphotrophic AND virus AND type AND 1) OR (Trichophyton AND tonsurans) OR (Chlamydia AND pneumoniae) OR (Parainfluenza AND Virus AND 5) OR (Leptospira AND interrogans AND sensu AND lato,) OR (Epidermophyton AND floccosum) OR (Malassezia AND sympodialis) OR (Lymphogranuloma AND venereum) OR (Moraxella AND catarrhalis) OR (West AND Nile AND Virus) OR (Adenoviruses) OR (Rhizopus AND rhizopodiformis) OR (Epstein AND barr AND virus) OR (Rhinoviruses) OR (Pseudomonas AND species) OR (Exanthem AND subitum) OR (Borrelia AND valaisiana) OR (Japanese AND encephalitis AND virus) OR (Deer AND tick AND virus) OR (Ascaris AND lumbricoides) OR (Candida AND parapsilosis) OR (Polioviruses) OR (Bartonella AND washoensis) OR (Babesia AND microti) OR (Leptospira AND grippotyphosa) OR (Coccidioides AND posadasii) OR (Human AND herpesvirus AND 8) OR (Stenotrophomonas AND maltophilia) OR (Human AND papillomavirus AND type AND 16) OR (Coronaviruses) OR (Leptospira AND ballum) OR (Human AND herpesvirus AND 2) OR (Human AND herpesvirus AND 3) OR (Porphyromonas AND species) OR (Human AND herpesvirus AND 1) OR (Human AND herpesvirus AND 6) OR (Human AND herpesvirus AND 7) OR (Bartonella AND bacilliformis) OR (Human AND herpesvirus AND 5) OR (Borrelia AND hermsii) OR (Streptococcus AND pyogenes) OR (Streptococcus) OR (Bartonella AND quintana) OR (Coccidioides AND immitis) OR (Cytomegalovirus) OR (Rickettsia AND australis) OR (Echo AND viruses) OR (Rickettsia AND honei) OR (Treponema AND palladium) OR (Echinococcus AND granulosus) OR (Borrelia AND turicatae) OR (Leptospira AND icterohaemorrhagiae) OR (Shigella AND boydii) OR (Borrelia AND afzelii) OR (Hepatitis AND B AND virus) OR (Angiostrongylus AND cantonensis) OR (Ehrlichia AND canis) OR (Bartonella AND vinsonii) OR (Parainfluenza AND viruses) OR (Brugia AND timori) OR (Rickettsia AND massiliae) OR (Leishmania AND infantum) OR (Staphylococcus AND aureus) OR (Polio AND virus) OR (Adenovirus AND type AND 36) OR (Alcaligenes AND xylosoxidans) OR (Taenia AND solium) OR (Enterococcus AND faecium) OR (Vibrio AND vulnificus) OR (Treponema AND denticola) OR (Propionibacterium AND acnes) OR (Xenotropic AND murine AND leukemia AND virus-related AND virus) OR (Helicobacter AND pylori) OR (Influenza AND A AND virus) OR (Streptococcus AND peumoniae) OR (Streptococcus AND agalactiae) OR (Streptococcus AND pneumoniae) OR (Actinomyces AND viscosus) OR (Leptospira AND autumnalis) OR (Histoplasma AND capsulatum) OR (Enterococcus AND faecalis) OR (Variola AND minor AND virus) OR (Rabies AND virus) OR (Variola AND major AND virus) OR (Onchocerca AND volvulus) OR (Malassezia AND globosa) OR (Trichophyton AND rubrum) OR (Atopobium AND vaginae) OR (Porphyromonas AND endodontalis) OR (Human AND endogenous AND retrovirus AND HRES-1) OR (Dictyostelium AND discoideum) OR (Brucella AND abortus) OR (Brucella AND melitensis) OR (Shigella AND dysenteriae) OR (Ehrlichia AND chaffeensis) OR (Rickettsia AND prowazekii) OR (Fusobacterium AND periodonticum) OR (Aspergillus AND clavatus) OR (Enterobacter) OR (Malassezia AND restricta) OR (Barkholderia AND pseudomallei) OR (Prevotella AND tannerae) OR (Leishmania AND tropica AND mexicana) OR (Bacteroides AND species) OR (Pseudomonas AND aeruginosa) OR (Taenia AND saginata) OR (Enterobacter AND sakazakii) OR (influenza AND virus) OR (Filifactor AND alocis) OR (Campylobacter AND rectus) OR (Mycosis AND fungoides) OR (Avian AND leukosis AND virus) OR (Rickettsia AND conorii) OR (Curvularia AND lunata) OR (Brucella AND suis) OR (Hepatitis AND C AND virus) OR (Rickettsia AND rickettsii) OR (Leishmania AND aethiopica) OR (Borrelia AND duttoni) OR (Microsporum AND canis) OR (Human AND herpesvirus AND 6A) OR (Leprospira AND pomona) OR (St. AND Louis AND encephalitis AND virus) NOT phage" 
	def getSearcher():Searcher = {
      val parser = new PatternParser
	  val compiler = new PatternCompiler(parser,3)
      val access = getAccess()
	  val search = new Searcher(compiler,access)
      return search
  }
}

class OrgSearchTest extends FunSuite {
  test("Test Organism Term Search"){
    expect(596) {
      val s = SearchTestGlobals.getSearcher()
      s.searchOrgs(SearchTestGlobals.pathogenQS).getUniqueOrgs.toList.length
    }
  }
  test("Test Organism Query") {
    expect(210) {
      val s = SearchTestGlobals.getSearcher()
      s.search("AVHAD", SearchTestGlobals.pathogenQS).getUniqueOrgs().toList.length
    }
  }
  test("Test Organism Index"){
    expect(true) {
      val access = SearchTestGlobals.getAccess()
      val x = access.getIndexedTerms("org")
      x.hasPositions() && x.getDocCount() > 1
    }
  }
}

class SearchTest extends FunSuite {
  
	def isMatch(seq:String,query:String):Boolean = {
	  val mch = s".*(${query}).*".r
	  seq match {
	    case mch(s) => true
	    case _ => false
	  }
	}
  test("Test search basic") {
    expect(true) {
      val search = SearchTestGlobals.getSearcher()
      val query = "AVHADD[EA]{0,4}"
	  val sres = search.search(query)
	  val res = sres.getMatchingSeqs(0,10)
	  res.map( (x) => isMatch(x,query) ).reduce( (x,y) => x&&y )&& ( res.length == 10 )
	  //res.map( (x) => x.contains(query) )
    }
  }
  test("Test search with Ands") {
    expect(true){
      val search = SearchTestGlobals.getSearcher()
      val query = "AVH^ADD"
      val sres = search.search(query)
      val res = sres.getMatchingSeqs(0,10)
      res.map( (x) => isMatch(x,"AVH")&&isMatch(x,"ADD") ).reduce( (x,y) => x&&y )&& ( res.length == 10 )
    }
  }
  test("Test short search (AH)") {
    expect(true){
      val search = SearchTestGlobals.getSearcher()
      val query = "AH"
	  val sres = search.search(query)
	  val res = sres.getMatchingSeqs(0,10)
	  res.map( (x) => isMatch(x,query) ).reduce( (x,y) => x&&y )&& ( res.length == 10 )
    }
  }
  test("Test short search (AA.{1,1})") {
    expect(true){
      val search = SearchTestGlobals.getSearcher()
      val query = "AA.{1,1}"
	  val sres = search.search(query)
	  val res = sres.getMatchingSeqs(0,10)
	  res.map( (x) => isMatch(x,query) ).reduce( (x,y) => x&&y ) && ( res.length == 10 )
    }
  }
}

class ProtSearchTest extends FunSuite {
  test("Test protein search") {
    expect(true){
      val search = SearchTestGlobals.getSearcher()
      val query = "DAFEY"
      val sres=search.search(query)
      sres.getTopDocs.scoreDocs.length
      sres.getJSON(1, 10)
    }
  }
}

class FalciparumSearchTest extends FunSuite {
  test("Test Combination Search") {
    expect(677){
      val search = SearchTestGlobals.getSearcher()
      val query = "DAFEY"
      val sres=search.search(query)
      sres.getUniqueOrgs.toList.length
    }
  }
  test("Test Combination Search Orgs") {
    expect(true){
      val search = SearchTestGlobals.getSearcher()
      val r1 = "RLKEP"
      val s1 = "SNKQG"
      val d1 = "DAFEY"
      val r = "LKEP|R.KEP|RL.EP|RLK.P|RLKE|RLKEP"
      val s = "SNKQG|NKQG|S.KQG|SN.QG|SNK.G|SNKQ"
      val d = "DAFEY|AFEY|D.FEY|DA.EY|DAF.Y|DAFE"
      val query = "LKEP|R.KEP|RL.EP|RLK.P|RLKE"
      //val sres=search.search(r++"^"++d++"^"++s)
      //sres.nResults
      val pathogens = Set("Helicobacter pylori J99","Human papillomavirus type 96","Papiine herpesvirus 2","Listeria monocytogenes 10403S","Chlamydia trachomatis L1/440/LN","Enterococcus faecalis str. Symbioflor 1","Acinetobacter baumannii D1279779","Streptococcus pneumoniae TIGR4","Listeria monocytogenes SLCC2376","Equid herpesvirus 9","Influenza A virus (A/goose/Guangdong/1/1996(H5N1))","Human bocavirus 4","Clostridium perfringens SM101","Mycoplasma hominis ATCC 23114","Helicobacter pylori Puno135","Campylobacter jejuni subsp. doylei 269.97","Salmonella enterica subsp. enterica serovar Typhimurium str. D23580","Human papillomavirus - 2","Legionella pneumophila subsp. pneumophila str. Lorraine","Burkholderia cenocepacia J2315","Haemophilus influenzae PittGG","Rickettsia parkeri str. Portsmouth","Porphyromonas gingivalis TDC60","Prevotella intermedia 17","Neisseria meningitidis G2136","Streptococcus agalactiae GD201008-001","Acinetobacter baumannii SDF","Borrelia burgdorferi ZS7","Campylobacter jejuni subsp. jejuni 81116","Respiratory syncytial virus","Streptococcus pneumoniae SPN034156","Rhinovirus A","Tropheryma whipplei str. Twist","Neisseria gonorrhoeae FA 1090","Tropheryma whipplei TW08/27","Murid herpesvirus 1","Corynebacterium diphtheriae PW8","Chlamydia trachomatis L2b/Canada1","Helicobacter pylori Shi470","Rickettsia prowazekii str. Madrid E","Yersinia pestis KIM10+","Propionibacterium acnes HL096PA1","Streptococcus agalactiae 09mas018883","Human bocavirus 3","Chlamydia trachomatis G/9768","Legionella pneumophila subsp. pneumophila","Cercopithecine herpesvirus 5","Capnocytophaga ochracea DSM 7271","Chlamydia trachomatis D/SotonD6","Klebsiella pneumoniae subsp. pneumoniae HS11286","Helicobacter pylori Rif1","Human papillomavirus type 48","Helicobacter pylori SNT49","Human parainfluenza virus 3","Macacine herpesvirus 5","Neisseria gonorrhoeae TCDC-NG08107","Chlamydia trachomatis E/SW3","Hepatitis C virus","Bovine parainfluenza virus 3","Dengue virus 2","Human papillomavirus type 26","Helicobacter pylori UM037","Astrovirus MLB1","Chlamydophila psittaci RD1","Streptococcus pneumoniae AP200","Chlamydia trachomatis L1/115","Chlamydophila pneumoniae TW-183","Vibrio parahaemolyticus RIMD 2210633","Vibrio vulnificus YJ016","Chlamydophila psittaci 08DC60","Borrelia garinii BgVir","Mycoplasma fermentans PG18","Streptococcus pneumoniae INV200","Chlamydia trachomatis Ia/SotonIa3","Salmonella enterica subsp. enterica serovar Typhimurium str. 14028S","Human papillomavirus - 18","Human herpesvirus 8","Yersinia pestis D106004","Streptococcus pneumoniae D39","Human papillomavirus type 49","Campylobacter jejuni subsp. jejuni ICDCCJ07001","Heron hepatitis B virus","Human bocavirus","Listeria monocytogenes SLCC2479","Brucella melitensis biovar Abortus 2308","Duck hepatitis B virus","Listeria monocytogenes ATCC 19117","Human bocavirus 2","Human papillomavirus type 92","Mycoplasma pneumoniae 309","Vibrio cholerae MJ-1236","Corynebacterium diphtheriae HC03","Macacine herpesvirus 4","Haemophilus influenzae 86-028NP","Borrelia valaisiana VS116","Listeria monocytogenes SLCC2372","Streptococcus pneumoniae Hungary19A-6","Campylobacter jejuni subsp. jejuni M1","Chlamydia trachomatis L2b/UCH-2","Chlamydia trachomatis B/TZ1A828/OT","Bartonella henselae str. Houston-1","Helicobacter pylori Gambia94/24","Human papillomavirus type 90","Rickettsia africae ESF-5","Human papillomavirus type 108","Rickettsia rickettsii str. Brazil","Cercopithecine herpesvirus 2","Influenza A virus (A/New York/392/2004(H3N2))","Streptococcus agalactiae NEM316","Brucella abortus A13334","Chlamydia trachomatis 434/Bu","Chlamydia trachomatis L2b/CV204","Suid herpesvirus 1","Listeria monocytogenes SLCC7179","Streptococcus agalactiae","Yersinia pestis biovar Microtus str. 91001","Streptococcus pyogenes MGAS2096","Rickettsia rickettsii str. Hlp#2","Neisseria meningitidis alpha14","Ostreid herpesvirus 1","Chlamydia trachomatis E/SotonE8","Fusobacterium nucleatum subsp. animalis 4_8","Streptococcus pneumoniae 670-6B","Rickettsia rickettsii str. \'Sheila Smith\'","Chlamydia trachomatis L2b/Ams5","Vibrio cholerae IEC224","Chlamydia trachomatis E/150","Klebsiella pneumoniae KCTC 2242","Human papillomavirus type 9","Propionibacterium acnes TypeIA2 P.acn31","Chlamydia trachomatis G/SotonG1","Enterococcus faecium NRRL B-2354","Helicobacter pylori PeCan4","Murine norovirus 1","Neorickettsia sennetsu str. Miyayama","Brucella suis ATCC 23445","Helicobacter pylori OK310","Duck astrovirus C-NGB","Acinetobacter baumannii 1656-2","Streptococcus pneumoniae TCH8431/19A","Mycoplasma fermentans M64","Campylobacter jejuni subsp. jejuni NCTC 11168-BN148","Corynebacterium diphtheriae 241","Chlamydia trachomatis A/363","Streptococcus pneumoniae gamPNI0373","Corynebacterium diphtheriae CDCE 8392","Legionella pneumophila 2300/99 Alcoy","Rickettsia prowazekii str. Breinl","Influenza A virus (A/Korea/426/68(H2N2))","Rickettsia conorii str. Malish 7","Bartonella quintana RM-11","Chlamydia trachomatis Ia/SotonIa1","Hepatitis C virus genotype 3","Human papillomavirus type 32","Streptococcus agalactiae A909","Rickettsia rickettsii str. Iowa","Chlamydia trachomatis L2b/Ams4","Streptococcus pneumoniae G54","Human papillomavirus type 10","Brucella abortus S19","Chlamydia trachomatis IU888","Streptococcus pyogenes MGAS6180","Rickettsia prowazekii str. Chernikova","Shigella boydii Sb227","Murid herpesvirus 4","Streptococcus pneumoniae OXC141","Avian leukosis virus","Clostridium perfringens str. 13","Chlamydia trachomatis L2b/Ams2","Helicobacter pylori SouthAfrica7","Helicobacter pylori HPAG1","Brucella canis ATCC 23365","Corynebacterium diphtheriae 31A","Chlamydia trachomatis D/SotonD1","Helicobacter pylori Sat464","Mycoplasma pneumoniae M129-B7","Helicobacter pylori Shi169","Influenza A virus (A/Korea/426/1968(H2N2))","Bacteroides fragilis YCH46","Bovine herpesvirus 5","Vibrio cholerae O395","Powassan virus","Salmonella enterica subsp. enterica serovar Typhimurium str. 798","Burkholderia cenocepacia HI2424","Borrelia recurrentis A1","Helicobacter pylori UM032","Hepatitis C virus genotype 2","HMO Astrovirus A","Ranid herpesvirus 1","Clostridium difficile CD196","Acinetobacter baumannii AB0057","Haemophilus influenzae R2846","Helicobacter pylori 51","Chlamydia trachomatis L2b/Ams3","Rickettsia prowazekii str. Katsinyian","Mopeia Lassa virus reassortant 29","Corynebacterium diphtheriae NCTC 13129","Bacteroides vulgatus ATCC 8482","Burkholderia cenocepacia MC0-3","Mumps virus","Bordetella parapertussis Bpp5","Streptococcus agalactiae ILRI112","Chlamydia trachomatis E/SotonE4","Human herpesvirus 3","Helicobacter pylori India7","Porphyromonas gingivalis W83","Haemophilus influenzae PittEE","Neisseria meningitidis 8013","Streptococcus pyogenes MGAS10270","Bordetella pertussis 18323","Rickettsia bellii OSU 85-389","Salmonella enterica subsp. enterica serovar Typhimurium str. ST4/74","Influenza A virus (A/Hong Kong/1073/99(H9N2))","Yersinia pestis Antiqua","Streptococcus pyogenes Alab49","Human papillomavirus type 5","Haemophilus influenzae Rd KW20","Turkey astrovirus","Chlamydia trachomatis G/11222","Propionibacterium acnes KPA171202","Dengue virus 4","Chlamydia trachomatis L2b/LST","Human papillomavirus type 103","Fusobacterium nucleatum subsp. polymorphum ATCC 10953","Streptococcus pyogenes MGAS5005","Rickettsia japonica YH","Helicobacter pylori 2017","Yersinia pestis D182038","Chlamydophila pneumoniae LPCoLN","callitrichine herpesvirus 3","Chlamydia trachomatis L2c","Chlamydia trachomatis F/SW5","Murid herpesvirus 2","Streptococcus pyogenes MGAS315","Human papillomavirus type 53","Helicobacter pylori 26695","Chlamydia trachomatis D-LC","Chlamydia trachomatis Sweden2","Human papillomavirus type 4","Chlamydia trachomatis A/7249","Neisseria meningitidis NZ-05/33","Helicobacter pylori Rif2","Borrelia garinii NMJW1","Human papillomavirus type 41","Yersinia pestis Nepal516","Bocavirus gorilla/GBoV1/2009","Acinetobacter baumannii ATCC 17978","Dengue virus 3","Acinetobacter baumannii AB307-0294","Neisseria meningitidis FAM18","Acinetobacter baumannii ACICU","Helicobacter pylori B8","Streptococcus pyogenes SSI-1","Clostridium difficile R20291","Francisella tularensis subsp. tularensis TIGB03","Neisseria meningitidis alpha710","Listeria monocytogenes HCC23","Treponema denticola ATCC 35405","Snow goose hepatitis B virus","Chlamydophila psittaci Mat116","Campylobacter jejuni subsp. jejuni IA3902","Chlamydia trachomatis F/SW4","Campylobacter jejuni RM1221","Chlamydophila pneumoniae CWL029","Shigella flexneri 2a str. 2457T","Human herpesvirus 1","Francisella tularensis subsp. holarctica FTNF002-00","Bovine respiratory syncytial virus","Brucella melitensis M28","Felid herpesvirus 1","Acinetobacter baumannii BJAB0868","Mycobacterium leprae TN","Neisseria meningitidis MC58","Streptococcus pneumoniae JJA","Anguillid herpesvirus 1","Francisella tularensis subsp. tularensis NE061598","Francisella tularensis subsp. holarctica OSU18","Corynebacterium diphtheriae BH8","Corynebacterium diphtheriae HC04","Propionibacterium acnes TypeIA2 P.acn17","Bordetella pertussis Tohama I","Brucella canis HSK A52141","Yersinia pestis biovar Medievalis str. Harbin 35","Helicobacter pylori Puno120","Astrovirus MLB1 HK05","Anaplasma phagocytophilum HZ","Rickettsia felis URRWXCal2","Clostridium difficile BI1","Brucella suis VBI22","Borrelia afzelii PKo","Chlamydophila psittaci 01DC11","Corynebacterium diphtheriae HC02","Campylobacter jejuni subsp. jejuni NCTC 11168 = ATCC 700819","Human parainfluenza virus 1","Macacine herpesvirus 3","Chlamydia trachomatis A2497","Streptococcus pneumoniae ST556","Chlamydia trachomatis D-EC","Ehrlichia canis str. Jake","Rickettsia massiliae MTU5","Stenotrophomonas maltophilia K279a","Human papillomavirus type 63","Streptococcus pneumoniae 70585","Streptococcus pyogenes MGAS10394","Helicobacter pylori SJM180","Mycoplasma fermentans JER","Brucella melitensis ATCC 23457","Helicobacter pylori Lithuania75","Streptococcus pneumoniae SPN994039","Gardnerella vaginalis 409-05","Bacteroides fragilis 638R","Filifactor alocis ATCC 35896","Streptococcus pyogenes MGAS8232","Haemophilus influenzae R2866","Clostridium difficile 630","Listeria monocytogenes SLCC5850","Chlamydophila psittaci 6BC","Francisella tularensis subsp. tularensis SCHU S4","Chlamydia trachomatis G/11074","Acinetobacter baumannii MDR-ZJ06","Shigella dysenteriae Sd197","Human herpesvirus 4 type 2","Salmonella enterica subsp. enterica serovar Typhimurium str. T000240","Helicobacter pylori F16","Bovine herpesvirus 1","Helicobacter pylori v225d","Dengue virus 1","Rickettsia bellii RML369-C","Campylobacter jejuni subsp. jejuni 81-176","Sheldgoose hepatitis B virus","Corynebacterium diphtheriae HC01","Bacteroides fragilis NCTC 9343","Brucella melitensis M5-90","Streptococcus pyogenes A20","Saimiriine herpesvirus 2","Influenza B virus","Neisseria meningitidis M01-240149","Legionella pneumophila subsp. pneumophila str. Philadelphia 1","Human herpesvirus 7","Borrelia burgdorferi JD1","Mycoplasma pneumoniae FH","Enterococcus faecium DO","Astrovirus VA1","Streptococcus agalactiae ILRI005","Streptococcus pneumoniae SPN994038","Enterococcus faecalis OG1RF","Legionella pneumophila str. Paris","Mycobacterium ulcerans Agy99","Ictalurid herpesvirus 1","Chlamydophila pneumoniae J138","Enterococcus faecalis V583","Human papillomavirus type 6b","Neisseria meningitidis M01-240355","Cercopithecine herpesvirus 9","Human papillomavirus type 60","Rubella virus","Yersinia pestis Z176003","Helicobacter pylori 35A","Helicobacter pylori XZ274","Shigella flexneri 2a str. 301","Francisella tularensis subsp. holarctica FSC200","Rickettsia prowazekii str. Rp22","Equid herpesvirus 4","Gardnerella vaginalis ATCC 14019","Acinetobacter baumannii BJAB07104","Chlamydia trachomatis F/SotonF3","Chlamydia trachomatis IU824","Propionibacterium acnes SK137","Listeria monocytogenes Finland 1998","Human respiratory syncytial virus","Chlamydia trachomatis L2/25667R","Macacine herpesvirus 1","Shigella flexneri 2002017","Vibrio parahaemolyticus BB22OP","Tupaiid herpesvirus 1","Neisseria meningitidis M04-240196","Moraxella catarrhalis RH4","Campylobacter jejuni subsp. jejuni PT14","Corynebacterium diphtheriae VA01","Klebsiella pneumoniae 342","Bartonella clarridgeiae 73","Japanese encephalitis virus","Chlamydia trachomatis K/SotonK1","Listeria monocytogenes SLCC2378","Human papillomavirus type 16","Measles virus","Rickettsia prowazekii str. Dachau","Shigella flexneri 5 str. 8401","Acinetobacter baumannii MDR-TJ","Human herpesvirus 4","West Nile virus","Helicobacter pylori Cuz20","Chlamydia trachomatis L3/404/LN","Chlamydia trachomatis E/11023","Corynebacterium diphtheriae INCA 402","Chlamydophila psittaci 02DC15","Human rhinovirus B14","Stenotrophomonas maltophilia R551-3","Rickettsia prowazekii str. BuV67-CWPP","Propionibacterium acnes C1","Helicobacter pylori UM299","Simian virus 40","Yersinia pestis Angola","Plasmodium falciparum 3D7","Neisseria meningitidis H44/76","Ross\'s goose hepatitis B virus","Chlamydophila pneumoniae AR39","Acinetobacter baumannii TYTH-1","Legionella pneumophila str. Lens","Mycoplasma arthritidis 158L3-1","Mycoplasma penetrans HF-2","Neisseria meningitidis Z2491","Equid herpesvirus 1","Salmonella enterica subsp. enterica serovar Typhimurium str. UK-1","Human herpesvirus 5","Borrelia garinii PBi","Helicobacter pylori Shi417","Helicobacter pylori HUP-B14","Hepatitis GB virus B","Chlamydia trachomatis E/Bour","Helicobacter pylori 2018","Francisella tularensis subsp. tularensis TI0902","Streptococcus pneumoniae Taiwan19F-14","Legionella pneumophila subsp. pneumophila str. Thunder Bay","Streptococcus pyogenes str. Manfredo","La Crosse virus","Helicobacter pylori PeCan18","Chlamydia trachomatis L2b/Ams1","Human parvovirus B19","Bartonella bacilliformis KC583","Yersinia pestis A1122","Streptococcus pyogenes MGAS9429","Bartonella grahamii as4aup","Chlamydia trachomatis","Francisella tularensis subsp. holarctica LVS","Klebsiella pneumoniae NTUH-K2044","Human papillomavirus - 1","Enterococcus faecalis 62","Neisseria meningitidis WUE 2594","Helicobacter pylori P12","Streptococcus pyogenes M1 GAS","Streptococcus pneumoniae SPN034183","Bovine herpesvirus 4","Chlamydia trachomatis A/HAR-13","Borrelia hermsii HS1","Klebsiella pneumoniae subsp. pneumoniae 1084","Novosphingobium aromaticivorans DSM 12444","Avian metapneumovirus","Turkey astrovirus 2","Chlamydia trachomatis L2b/Canada2","Yersinia pestis Pestoides F","Mycobacterium avium subsp. paratuberculosis MAP4","Alcelaphine herpesvirus 1","Chlamydia trachomatis L1/1322/p2","Chlamydia trachomatis L1/224","Rickettsia prowazekii str. GvV257","Ovine herpesvirus 2","Helicobacter pylori UM066","Streptococcus pyogenes MGAS1882","Human papillomavirus type 101","Legionella pneumophila subsp. pneumophila ATCC 43290","Brucella abortus bv. 1 str. 9-941","Human herpesvirus 2","Influenza C virus (C/Ann Arbor/1/50)","Rickettsia australis str. Cutlack","Haemophilus influenzae F3047","Rickettsia akari str. Hartford","Rickettsia prowazekii str. NMRC Madrid E","Chlamydia trachomatis D/SotonD5","Borrelia turicatae 91E135","Helicobacter pylori G27","Panine herpesvirus 2","Helicobacter pylori 83","Cyprinid herpesvirus 3","Gallid herpesvirus 3","Human papillomavirus type 88","Streptococcus pneumoniae INV104","Brucella melitensis bv. 1 str. 16M","Chicken astrovirus","Meleagrid herpesvirus 1","Helicobacter pylori F57","Hepatitis C virus genotype 6","Chlamydia trachomatis L2b/795","Rickettsia rickettsii str. Hino","Rickettsia prowazekii str. RpGvF24","Gallid herpesvirus 1","Helicobacter pylori Aklavik117","Human parainfluenza virus 2","Helicobacter pylori Shi112","Propionibacterium acnes TypeIA2 P.acn33","Chlamydia trachomatis L2b/8200/07","Mycoplasma pneumoniae M129","Chlamydia trachomatis B/Jali20/OT","Wolbachia endosymbiont strain TRS of Brugia malayi","Bordetella parapertussis 12822","Propionibacterium acnes ATCC 11828","Rift Valley fever virus","Rickettsia canadensis str. CA410","Streptococcus pyogenes M1 476","Porphyromonas gingivalis ATCC 33277","Listeria monocytogenes EGD-e","Parainfluenza virus 5","Rickettsia massiliae str. AZT80","Vibrio vulnificus CMCP6","Listeria monocytogenes SLCC2540","Streptococcus pyogenes MGAS15252","Haemophilus influenzae 10810","Gallid herpesvirus 2","Ureaplasma urealyticum serovar 10 str. ATCC 33699","Human papillomavirus type 50","Francisella tularensis subsp. holarctica F92","Neisseria gonorrhoeae NCCP11945","Hepatitis C virus genotype 5","Human papillomavirus type 34","Salmonella enterica subsp. enterica serovar Typhimurium str. LT2","Lassa virus","Acinetobacter baumannii TCDC-AB0715","Gardnerella vaginalis HMP9231","Acinetobacter baumannii AYE","Corynebacterium diphtheriae C7 (beta)","Rickettsia rickettsii str. Arizona","Yellow fever virus","Streptococcus pneumoniae P1031","Hepatitis E virus","Burkholderia multivorans ATCC 17616","St. Louis encephalitis virus","Streptococcus agalactiae 2603V/R","Salmonella enterica subsp. enterica serovar Typhimurium str. SL1344","Helicobacter pylori 52","Borrelia burgdorferi N40","Plasmodium falciparum","Helicobacter pylori F32","Francisella tularensis subsp. tularensis FSC198","Borrelia burgdorferi B31","Listeria monocytogenes","Streptococcus pneumoniae R6","Acinetobacter baumannii BJAB0715","Clostridium perfringens ATCC 13124","Helicobacter pylori F30","Ateline herpesvirus 3","Listeria monocytogenes J0161","Burkholderia cenocepacia AU 1054","Enterococcus faecium Aus0004","Legionella pneumophila str. Corby","Borrelia afzelii HLJ01","Helicobacter pylori 908","Hepatitis C virus genotype 4","Mycobacterium leprae Br4923","Vibrio cholerae LMA3984-4","Bartonella vinsonii subsp. berkhoffii str. Winnie","Fusobacterium nucleatum subsp. nucleatum ATCC 25586","Human metapneumovirus","Ehrlichia chaffeensis str. Arkansas","Haemophilus influenzae F3031","Streptococcus pneumoniae SPNA45","Vibrio cholerae M66-2","Streptococcus pneumoniae ATCC 700669","Francisella tularensis subsp. mediasiatica FSC147","Bordetella pertussis CS","Propionibacterium acnes 6609","Stenotrophomonas maltophilia JV3","Shigella boydii CDC 3083-94","Campylobacter jejuni subsp. jejuni S3","Brucella melitensis NI","Helicobacter pylori ELS37","Rhinovirus C","Human papillomavirus type 7","Rickettsia canadensis str. McKiel","Francisella tularensis subsp. tularensis WY96-3418","Vibrio vulnificus MO6-24/O","Chlamydia trachomatis G/9301","Brucella suis 1330","Streptococcus pyogenes MGAS10750","Ranid herpesvirus 2","Helicobacter pylori Aklavik86","Neisseria meningitidis 053442","Salmonella enterica subsp. enterica serovar Typhimurium str. U288","Psittacid herpesvirus 1","Propionibacterium acnes 266","Chlamydophila psittaci C19/98","Helicobacter pylori B38","Streptococcus agalactiae SA20-06","Stenotrophomonas maltophilia D457","Chlamydia trachomatis A/5291","Yersinia pestis CO92","Hepatitis B virus","Equid herpesvirus 2","Rabies virus","Rickettsia rickettsii str. Hauke","Human parvovirus 4","Streptococcus pneumoniae CGSP14","Enterococcus faecalis D32","Streptococcus pyogenes NZ131","Bartonella quintana str. Toulouse","Helicobacter pylori OK113","Rickettsia rickettsii str. Colombia")
      val r80 = search.search(r)
      val s80 = search.search(s)
      val d80 = search.search(d)
      
      val rd80 = search.search(r++"^"++d)
      val rs80 = search.search(r++"^"++s)
      val ds80 = search.search(d++"^"++s)
      
      val rds80 = search.search(r++"^"++s++"^"++d)
      
      val r100 = search.search(r1)
      val s100 = search.search(s1)
      val d100 = search.search(d1)
      
      val rs100 = search.search(r1++"^"++s1)
      val rd100 = search.search(r1++"^"++d1)
      val ds100 = search.search(d1++"^"+s1)
      
      val rds100 = search.search(r1++"^"++d1++"^"++s1)
      
      def count(res:Result):Int = {res.getOrgNames.filter( (a) => pathogens.contains(a) ).length}
      def countOrgs(res:Result):Int = {res.getUniqueOrgs().intersect(pathogens).toList.length}
      def mergeCount(r1:Result,r2:Result):Int = {r1.getUniqueOrgs().intersect(r2.getUniqueOrgs()).intersect(pathogens).toList.length}
      def merge2Count(r1:Result,r2:Result,r3:Result):Int = {
        r1.getUniqueOrgs().intersect(r2.getUniqueOrgs()).intersect(r3.getUniqueOrgs()).intersect(pathogens).toList.length
      }
      def merge2(r1:Result,r2:Result,r3:Result):Array[String] = {
        r1.getUniqueOrgs().intersect(r2.getUniqueOrgs()).intersect(r3.getUniqueOrgs()).intersect(pathogens).toArray
      }
      println(s"PROTEINS 100%: r:${count(r100)} s:${count(s100)} d:${count(d100)} " +
      		s"rs:${count(rs100)} rd:${count(rd100)} sd:${count(ds100)} srd: ${count(rds100)}")
      
      println(s"PROTEINS 80%: r:${count(r80)} s:${count(s80)} d:${count(d80)} " +
      		s"rs:${count(rs80)} rd:${count(rd80)} sd:${count(ds80)} srd: ${count(rds80)}")
      println(s"RDS80: ${rds80.getOrgNames.filter( (a) => pathogens.contains(a) ).toList}")
      println(s"RS100: ${rs100.getOrgNames()(0)}")
      
      println(s"ORGANISMS 100%: r:${countOrgs(r100)} s:${countOrgs(s100)} d:${countOrgs(d100)} " +
      		s"rs:${mergeCount(r100,s100)} rd:${mergeCount(r100,d100)} sd:${mergeCount(s100,d100)} srd: ${merge2Count(r100,s100,d100)}")
      println(s"RDS_ORGS100: ${r100.mergeOrgs(s100).mergeOrgs(d100).getJSON(0, 100)}")
      		
      println(s"ORGANISMS 80%: r:${countOrgs(r80)} s:${countOrgs(s80)} d:${countOrgs(d80)} " +
      		s"rs:${mergeCount(r80,s80)} rd:${mergeCount(r80,d80)} sd:${mergeCount(s80,d80)} srd: ${merge2Count(r80,s80,d80)}")
      def printToFile(content: String, location: String = "C:/Users/jtdoe/Desktop/WorkSheet.txt") =
      	Some(new java.io.PrintWriter(location)).foreach{f => try{f.write(content)}finally{f.close}}
      val res = rds80.getJSON(0, 100000)
      //printToFile(res,"/home/josh/FalciOut.txt")
    }
  }
}
