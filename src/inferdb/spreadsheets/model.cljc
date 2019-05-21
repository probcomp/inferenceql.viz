(ns inferdb.spreadsheets.model
  (:require [inferdb.multimixture.dsl :refer [multi-mixture view clusters]]
            [inferdb.cgpm.main :as cgpm]
            [metaprob.distributions :as dist]))

(def cluster-data
  [0.209195402299 {"percent_white" [0.8291806283815729, 0.07767175323794767]
                   "percent_lths" [0.08736262969918202, 0.020539696633234183]
                   "percent_children" [0.21483985610370215, 0.020025567222714924]
                   "percent_married" [0.51499742123408, 0.018564294097441793]
                   "percent_poor" [0.11410387097064298, 0.024223117478141824]
                   "percent_manufacturing" [0.11736463469952174, 0.05061261889253003]
                   "alex_type" [{"rural" 0.3224498568239661, "bipolar" 0.220408309470661, "western_ag" 0.06734598844070341, "center_right" 0.3224498568239661, "diverse" 0.06734598844070341}]
                   "cluster_name" [{"Rural populists" 0.3181818181818182, "Romney Republicans" 0.09090909090909091, "White moderates" 0.22727272727272727, "Diverse, educated suburbs" 0.045454545454545456, "Loyal GOP suburbs" 0.2727272727272727, "Emerging resistance" 0.045454545454545456}]
                   "nyt_rating" [{"3) Lean R" 0.07142857142857142, "4) Toss Up" 0.07142857142857142, "1) Solid R" 0.5204081632653061, "2) Likely R" 0.14285714285714285, "5) Lean D" 0.02040816326530612, "7) Solid D" 0.14285714285714285, "6) Likely D" 0.030612244897959183}]
                   "percent_single_parent" [0.08518306559955097, 0.00935723261428304]
                   "percent_owned" [0.7109121816538051, 0.03984662961960551]}
   0.064367816092 {"percent_white" [0.7345812452805682, 0.10091412007297115]
                   "percent_lths" [0.07262512454855113, 0.014838509950497738]
                   "percent_children" [0.2470993928088281, 0.029341690406627347]
                   "percent_married" [0.5704208498794799, 0.016099734992748634]
                   "percent_poor" [0.07019134228201385, 0.017269553765077366]
                   "percent_manufacturing" [0.10949536142711502, 0.03631620537665222]
                   "alex_type" [{"rural" 0.07096676784129781, "bipolar" 0.07096676784129781, "western_ag" 0.07096676784129781, "center_right" 0.6623690819020162, "diverse" 0.12473061457409039}]
                   "cluster_name" [{"Rural populists" 0.05263157894736842, "Romney Republicans" 0.5789473684210527, "White moderates" 0.05263157894736842, "Diverse, educated suburbs" 0.05263157894736842, "Loyal GOP suburbs" 0.21052631578947367, "Emerging resistance" 0.05263157894736842}]
                   "nyt_rating" [{"3) Lean R" 0.11428571428571428, "4) Toss Up" 0.22857142857142856, "1) Solid R" 0.2857142857142857, "2) Likely R" 0.11428571428571428, "5) Lean D" 0.08571428571428572, "7) Solid D" 0.11428571428571428, "6) Likely D" 0.05714285714285714}]
                   "percent_single_parent" [0.07672309126187549, 0.01572741132026293]
                   "percent_owned" [0.7429495161307428, 0.04033587049983664]}
   0.16091954023 {"percent_white" [0.7634817635804046, 0.09375305252173316]
                  "percent_lths" [0.13091558137755005, 0.025472653804359188]
                  "percent_children" [0.22813374859902105, 0.016030422353023842]
                  "percent_married" [0.4958716786382452, 0.02147522355018946]
                  "percent_poor" [0.1551222325664659, 0.02217580332392097]
                  "percent_manufacturing" [0.14685745688352306, 0.046358873399351956]
                  "alex_type" [{"rural" 0.2188681744686311, "bipolar" 0.2188681744686311, "western_ag" 0.12452730212547564, "center_right" 0.3132090468117866, "diverse" 0.12452730212547564}]
                  "cluster_name" [{"Rural populists" 0.2727272727272727, "Romney Republicans" 0.09090909090909091, "White moderates" 0.09090909090909091, "Diverse, educated suburbs" 0.18181818181818182, "Loyal GOP suburbs" 0.2727272727272727, "Emerging resistance" 0.09090909090909091}]
                  "nyt_rating" [{"3) Lean R" 0.06493506493506493, "4) Toss Up" 0.03896103896103896, "1) Solid R" 0.7792207792207793, "2) Likely R" 0.03896103896103896, "5) Lean D" 0.012987012987012988, "7) Solid D" 0.05194805194805195, "6) Likely D" 0.012987012987012988}]
                  "percent_single_parent" [0.10626119810794765, 0.01088091429606756]
                  "percent_owned" [0.6828392308579165, 0.03234355504673573]}
   0.0413793103448 {"percent_white" [0.36022690840172206, 0.10731723611175731]
                    "percent_lths" [0.17069458228538043, 0.029963649273374193]
                    "percent_children" [0.23654551302766014, 0.013447599492056768]
                    "percent_married" [0.35135004439580997, 0.02092637888726412]
                    "percent_poor" [0.2364055158496911, 0.02947576164734947]
                    "percent_manufacturing" [0.10069256360891703, 0.03711008980630603]
                    "alex_type" [{"rural" 0.2, "bipolar" 0.2, "western_ag" 0.2, "center_right" 0.2, "diverse" 0.2}]
                    "cluster_name" [{"Rural populists" 0.16666666666666666, "Romney Republicans" 0.16666666666666666, "White moderates" 0.16666666666666666, "Diverse, educated suburbs" 0.16666666666666666, "Loyal GOP suburbs" 0.16666666666666666, "Emerging resistance" 0.16666666666666666}]
                    "nyt_rating" [{"3) Lean R" 0.04, "4) Toss Up" 0.04, "1) Solid R" 0.04, "2) Likely R" 0.04, "5) Lean D" 0.04, "7) Solid D" 0.76, "6) Likely D" 0.04}]
                    "percent_single_parent" [0.16602033847140413, 0.012214756558269093]
                    "percent_owned" [0.5259204986123266, 0.06120761798992017]}
   0.00229885057471 {"percent_white" [0.10881465928457798, 0.0001]
                     "percent_lths" [0.3210100486743294, 0.0001]
                     "percent_children" [0.2783729052362052, 0.0001]
                     "percent_married" [0.31041190256811935, 0.0001]
                     "percent_poor" [0.357169881440656, 0.0001]
                     "percent_manufacturing" [0.033649312372176624, 0.0001]
                     "alex_type" [{"rural" 0.2, "bipolar" 0.2, "western_ag" 0.2, "center_right" 0.2, "diverse" 0.2}]
                     "cluster_name" [{"Rural populists" 0.16666666666666666, "Romney Republicans" 0.16666666666666666, "White moderates" 0.16666666666666666, "Diverse, educated suburbs" 0.16666666666666666, "Loyal GOP suburbs" 0.16666666666666666, "Emerging resistance" 0.16666666666666666}]
                     "nyt_rating" [{"3) Lean R" 0.125, "4) Toss Up" 0.125, "1) Solid R" 0.125, "2) Likely R" 0.125, "5) Lean D" 0.125, "7) Solid D" 0.25, "6) Likely D" 0.125}]
                     "percent_single_parent" [0.2643215043666016, 0.0001]
                     "percent_owned" [0.12135678309303405, 0.0001]}
   0.0298850574713 {"percent_white" [0.16078269670830136, 0.05336406831363449]
                    "percent_lths" [0.24914294187765135, 0.04234660029108615]
                    "percent_children" [0.26541760491114086, 0.007155326563117302]
                    "percent_married" [0.42648769288224764, 0.018421784120369968]
                    "percent_poor" [0.1941836785557809, 0.023576091088344502]
                    "percent_manufacturing" [0.0850809778410922, 0.030387736937998816]
                    "alex_type" [{"rural" 0.2, "bipolar" 0.2, "western_ag" 0.2, "center_right" 0.2, "diverse" 0.2}]
                    "cluster_name" [{"Rural populists" 0.16666666666666666, "Romney Republicans" 0.16666666666666666, "White moderates" 0.16666666666666666, "Diverse, educated suburbs" 0.16666666666666666, "Loyal GOP suburbs" 0.16666666666666666, "Emerging resistance" 0.16666666666666666}]
                    "nyt_rating" [{"3) Lean R" 0.05, "4) Toss Up" 0.05, "1) Solid R" 0.05, "2) Likely R" 0.05, "5) Lean D" 0.05, "7) Solid D" 0.7, "6) Likely D" 0.05}]
                    "percent_single_parent" [0.16454554830739415, 0.011814227707783568]
                    "percent_owned" [0.48095491373908833, 0.06404239165579376]}
   0.119540229885 {"percent_white" [0.6919506994987028, 0.08467621980487666]
                   "percent_lths" [0.10317275432082174, 0.017074614687189175]
                   "percent_children" [0.20604498698578444, 0.01410195703821609]
                   "percent_married" [0.4524797473910044, 0.026477778446827634]
                   "percent_poor" [0.13641770251698782, 0.02498905167072906]
                   "percent_manufacturing" [0.09262267046031286, 0.03442213895370143]
                   "alex_type" [{"rural" 0.17058793162615346, "bipolar" 0.24411810256076982, "western_ag" 0.09705776069153713, "center_right" 0.31764827349538616, "diverse" 0.17058793162615346}]
                   "cluster_name" [{"Rural populists" 0.1875, "Romney Republicans" 0.0625, "White moderates" 0.375, "Diverse, educated suburbs" 0.125, "Loyal GOP suburbs" 0.125, "Emerging resistance" 0.125}]
                   "nyt_rating" [{"3) Lean R" 0.05084745762711865, "4) Toss Up" 0.05084745762711865, "1) Solid R" 0.06779661016949153, "2) Likely R" 0.0847457627118644, "5) Lean D" 0.05084745762711865, "7) Solid D" 0.6271186440677966, "6) Likely D" 0.06779661016949153}]
                   "percent_single_parent" [0.1000805834380685, 0.013920406139929672]
                   "percent_owned" [0.6298037917493293, 0.050262965113314274]}
   0.0367816091954 {"percent_white" [0.5199669201996089, 0.15850351521634307]
                    "percent_lths" [0.07782719071407124, 0.021823352422061557]
                    "percent_children" [0.20269201031495232, 0.018710742235411186]
                    "percent_married" [0.5016596730774447, 0.04294681985825608]
                    "percent_poor" [0.08575394835517697, 0.013765939624667834]
                    "percent_manufacturing" [0.08230520532556473, 0.040241834866684444]
                    "alex_type" [{"rural" 0.13749908582896675, "bipolar" 0.13749908582896675, "western_ag" 0.13749908582896675, "center_right" 0.45000365668413295, "diverse" 0.13749908582896675}]
                    "cluster_name" [{"Rural populists" 0.1111111111111111, "Romney Republicans" 0.2222222222222222, "White moderates" 0.1111111111111111, "Diverse, educated suburbs" 0.3333333333333333, "Loyal GOP suburbs" 0.1111111111111111, "Emerging resistance" 0.1111111111111111}]
                    "nyt_rating" [{"3) Lean R" 0.13043478260869565, "4) Toss Up" 0.08695652173913043, "1) Solid R" 0.043478260869565216, "2) Likely R" 0.08695652173913043, "5) Lean D" 0.043478260869565216, "7) Solid D" 0.5652173913043478, "6) Likely D" 0.043478260869565216}]
                    "percent_single_parent" [0.06663625547560023, 0.013010475046573048]
                    "percent_owned" [0.5703387545739341, 0.053615632923150316]}
   0.00919540229885 {"percent_white" [0.5104659243956282, 0.08622196645866992]
                     "percent_lths" [0.10438675708653598, 0.022870436703307614]
                     "percent_children" [0.12658572934198348, 0.027655379223579638]
                     "percent_married" [0.42676872057666293, 0.03209653408080069]
                     "percent_poor" [0.1343127557497189, 0.026915097993505433]
                     "percent_manufacturing" [0.04168341986955051, 0.008194662897276496]
                     "alex_type" [{"rural" 0.2, "bipolar" 0.2, "western_ag" 0.2, "center_right" 0.2, "diverse" 0.2}]
                     "cluster_name" [{"Rural populists" 0.16666666666666666, "Romney Republicans" 0.16666666666666666, "White moderates" 0.16666666666666666, "Diverse, educated suburbs" 0.16666666666666666, "Loyal GOP suburbs" 0.16666666666666666, "Emerging resistance" 0.16666666666666666}]
                     "nyt_rating" [{"3) Lean R" 0.09090909090909091, "4) Toss Up" 0.09090909090909091, "1) Solid R" 0.09090909090909091, "2) Likely R" 0.09090909090909091, "5) Lean D" 0.09090909090909091, "7) Solid D" 0.45454545454545453, "6) Likely D" 0.09090909090909091}]
                     "percent_single_parent" [0.04468439310555765, 0.010083898055811554]
                     "percent_owned" [0.2831158058853839, 0.029540935580986607]}
   0.00689655172414 {"percent_white" [0.10786021412027864, 0.008822698000045103]
                     "percent_lths" [0.31441307884814873, 0.009385212955613854]
                     "percent_children" [0.32425794887084314, 0.009783432708183566]
                     "percent_married" [0.49376020343766136, 0.0034842502780368803]
                     "percent_poor" [0.2710181880605522, 0.009419483354551186]
                     "percent_manufacturing" [0.055470848618053625, 0.00571858568373529]
                     "alex_type" [{"rural" 0.2, "bipolar" 0.2, "western_ag" 0.2, "center_right" 0.2, "diverse" 0.2}]
                     "cluster_name" [{"Rural populists" 0.16666666666666666, "Romney Republicans" 0.16666666666666666, "White moderates" 0.16666666666666666, "Diverse, educated suburbs" 0.16666666666666666, "Loyal GOP suburbs" 0.16666666666666666, "Emerging resistance" 0.16666666666666666}]
                     "nyt_rating" [{"3) Lean R" 0.1, "4) Toss Up" 0.1, "1) Solid R" 0.1, "2) Likely R" 0.1, "5) Lean D" 0.1, "7) Solid D" 0.4, "6) Likely D" 0.1}]
                     "percent_single_parent" [0.1693083762788078, 0.001589549202342174]
                     "percent_owned" [0.7054693315748021, 0.012172373091006813]}
   0.12183908046 {"percent_white" [0.5546670398003798, 0.11504500340661808]
                  "percent_lths" [0.1100448870246473, 0.022331440208529157]
                  "percent_children" [0.2404531398224461, 0.017077088633961447]
                  "percent_married" [0.5058530598447951, 0.021650613636655127]
                  "percent_poor" [0.1053991205946146, 0.01856840642966247]
                  "percent_manufacturing" [0.08200213388118072, 0.03058194030763431]
                  "alex_type" [{"rural" 0.08461434603638482, "bipolar" 0.2769237693090768, "western_ag" 0.08461434603638482, "center_right" 0.08461434603638482, "diverse" 0.4692331925817688}]
                  "cluster_name" [{"Rural populists" 0.0625, "Romney Republicans" 0.1875, "White moderates" 0.125, "Diverse, educated suburbs" 0.3125, "Loyal GOP suburbs" 0.0625, "Emerging resistance" 0.25}]
                  "nyt_rating" [{"3) Lean R" 0.06666666666666667, "4) Toss Up" 0.08333333333333333, "1) Solid R" 0.21666666666666667, "2) Likely R" 0.11666666666666667, "5) Lean D" 0.05, "7) Solid D" 0.43333333333333335, "6) Likely D" 0.03333333333333333}]
                  "percent_single_parent" [0.10145512410819421, 0.011822380439035237]
                  "percent_owned" [0.6339464493555753, 0.057238555468417184]}
   0.0528735632184 {"percent_white" [0.36771985398067725, 0.12001935335429915]
                    "percent_lths" [0.1355134322194705, 0.024934196953055494]
                    "percent_children" [0.2190945577869719, 0.015427385857770492]
                    "percent_married" [0.4198612534394261, 0.028552706544909153]
                    "percent_poor" [0.1631086925394695, 0.030576197545357127]
                    "percent_manufacturing" [0.06017426525465866, 0.03331698420735238]
                    "alex_type" [{"rural" 0.17368372431713577, "bipolar" 0.17368372431713577, "western_ag" 0.17368372431713577, "center_right" 0.17368372431713577, "diverse" 0.30526510273145696}]
                    "cluster_name" [{"Rural populists" 0.14285714285714285, "Romney Republicans" 0.14285714285714285, "White moderates" 0.14285714285714285, "Diverse, educated suburbs" 0.14285714285714285, "Loyal GOP suburbs" 0.14285714285714285, "Emerging resistance" 0.2857142857142857}]
                    "nyt_rating" [{"3) Lean R" 0.03333333333333333, "4) Toss Up" 0.06666666666666667, "1) Solid R" 0.03333333333333333, "2) Likely R" 0.03333333333333333, "5) Lean D" 0.03333333333333333, "7) Solid D" 0.7666666666666667, "6) Likely D" 0.03333333333333333}]
                    "percent_single_parent" [0.13831788180919724, 0.017158897122851156]
                    "percent_owned" [0.5493685060677015, 0.06443576159556708]}
   0.016091954023 {"percent_white" [0.15299620538891923, 0.062486181329497625]
                   "percent_lths" [0.3815258313380413, 0.04118490906501891]
                   "percent_children" [0.3110636523106668, 0.012991802753102053]
                   "percent_married" [0.40584217046957016, 0.03306061223922891]
                   "percent_poor" [0.24925086461499418, 0.027185522857970282]
                   "percent_manufacturing" [0.10641060979892195, 0.031879985404447024]
                   "alex_type" [{"rural" 0.17368372431713577, "bipolar" 0.17368372431713577, "western_ag" 0.30526510273145696, "center_right" 0.17368372431713577, "diverse" 0.17368372431713577}]
                   "cluster_name" [{"Rural populists" 0.14285714285714285, "Romney Republicans" 0.14285714285714285, "White moderates" 0.14285714285714285, "Diverse, educated suburbs" 0.14285714285714285, "Loyal GOP suburbs" 0.14285714285714285, "Emerging resistance" 0.2857142857142857}]
                   "nyt_rating" [{"3) Lean R" 0.07142857142857142, "4) Toss Up" 0.07142857142857142, "1) Solid R" 0.07142857142857142, "2) Likely R" 0.14285714285714285, "5) Lean D" 0.07142857142857142, "7) Solid D" 0.5, "6) Likely D" 0.07142857142857142}]
                   "percent_single_parent" [0.20337401145000938, 0.01651005877688891]
                   "percent_owned" [0.4430516858698502, 0.0565621132301581]}
   0.0137931034483 {"percent_white" [0.27812248052030936, 0.06483396743477399]
                    "percent_lths" [0.13092755972975356, 0.01769781059905434]
                    "percent_children" [0.19728475272206408, 0.015389435409469129]
                    "percent_married" [0.29901772376877467, 0.027194653273514384]
                    "percent_poor" [0.21758319854024846, 0.02303869329820788]
                    "percent_manufacturing" [0.062300871962577206, 0.021284377421532025]
                    "alex_type" [{"rural" 0.2, "bipolar" 0.2, "western_ag" 0.2, "center_right" 0.2, "diverse" 0.2}]
                    "cluster_name" [{"Rural populists" 0.16666666666666666, "Romney Republicans" 0.16666666666666666, "White moderates" 0.16666666666666666, "Diverse, educated suburbs" 0.16666666666666666, "Loyal GOP suburbs" 0.16666666666666666, "Emerging resistance" 0.16666666666666666}]
                    "nyt_rating" [{"3) Lean R" 0.07692307692307693, "4) Toss Up" 0.07692307692307693, "1) Solid R" 0.07692307692307693, "2) Likely R" 0.07692307692307693, "5) Lean D" 0.07692307692307693, "7) Solid D" 0.5384615384615384, "6) Likely D" 0.07692307692307693}]
                    "percent_single_parent" [0.14067385052735043, 0.008020321412235017]
                    "percent_owned" [0.40963148561471807, 0.06198802079613626]}
   0.0620689655172 {"percent_white" [0.5278662778025767, 0.08919137436996215]
                    "percent_lths" [0.16527041783476723, 0.02319289482514786]
                    "percent_children" [0.25576957296926106, 0.0149970367443409]
                    "percent_married" [0.45011614331108457, 0.02088919884193474]
                    "percent_poor" [0.1815875026009537, 0.03131022569698531]
                    "percent_manufacturing" [0.08042431405463032, 0.025098327347958763]
                    "alex_type" [{"rural" 0.13749908582896675, "bipolar" 0.13749908582896675, "western_ag" 0.3458354663990776, "center_right" 0.13749908582896675, "diverse" 0.24166727611402217}]
                    "cluster_name" [{"Rural populists" 0.1111111111111111, "Romney Republicans" 0.1111111111111111, "White moderates" 0.1111111111111111, "Diverse, educated suburbs" 0.1111111111111111, "Loyal GOP suburbs" 0.1111111111111111, "Emerging resistance" 0.4444444444444444}]
                    "nyt_rating" [{"3) Lean R" 0.058823529411764705, "4) Toss Up" 0.058823529411764705, "1) Solid R" 0.6764705882352942, "2) Likely R" 0.029411764705882353, "5) Lean D" 0.029411764705882353, "7) Solid D" 0.058823529411764705, "6) Likely D" 0.08823529411764706}]
                    "percent_single_parent" [0.12787393344507347, 0.012492777611701027]
                    "percent_owned" [0.6250847870212092, 0.036762927515449205]}
   0.00919540229885 {"percent_white" [0.3081741135477617, 0.05969055620447845]
                     "percent_lths" [0.16086954503686654, 0.005026430144744877]
                     "percent_children" [0.2035376843955829, 0.005564620382380095]
                     "percent_married" [0.4843704382779521, 0.04330082995740382]
                     "percent_poor" [0.14146594664189938, 0.00840119039184328]
                     "percent_manufacturing" [0.056949646539666776, 0.016162147134585803]
                     "alex_type" [{"rural" 0.17368372431713577, "bipolar" 0.17368372431713577, "western_ag" 0.17368372431713577, "center_right" 0.17368372431713577, "diverse" 0.30526510273145696}]
                     "cluster_name" [{"Rural populists" 0.14285714285714285, "Romney Republicans" 0.14285714285714285, "White moderates" 0.14285714285714285, "Diverse, educated suburbs" 0.14285714285714285, "Loyal GOP suburbs" 0.14285714285714285, "Emerging resistance" 0.2857142857142857}]
                     "nyt_rating" [{"3) Lean R" 0.09090909090909091, "4) Toss Up" 0.09090909090909091, "1) Solid R" 0.09090909090909091, "2) Likely R" 0.09090909090909091, "5) Lean D" 0.18181818181818182, "7) Solid D" 0.36363636363636365, "6) Likely D" 0.09090909090909091}]
                     "percent_single_parent" [0.0817660991181535, 0.011217926501809507]
                     "percent_owned" [0.5111500259052801, 0.03388011031564093]}
   0.0183908045977 {"percent_white" [0.27650858121748473, 0.09441042050536584]
                    "percent_lths" [0.22516693658594422, 0.022736685664361902]
                    "percent_children" [0.23613292399949015, 0.025409099624937523]
                    "percent_married" [0.4300742890187883, 0.01831593824378102]
                    "percent_poor" [0.13828776291398467, 0.02143548095448292]
                    "percent_manufacturing" [0.08743205943993215, 0.03128827406153941]
                    "alex_type" [{"rural" 0.15348761267220376, "bipolar" 0.15348761267220376, "western_ag" 0.386049549311185, "center_right" 0.15348761267220376, "diverse" 0.15348761267220376}]
                    "cluster_name" [{"Rural populists" 0.125, "Romney Republicans" 0.125, "White moderates" 0.125, "Diverse, educated suburbs" 0.125, "Loyal GOP suburbs" 0.125, "Emerging resistance" 0.375}]
                    "nyt_rating" [{"3) Lean R" 0.06666666666666667, "4) Toss Up" 0.2, "1) Solid R" 0.06666666666666667, "2) Likely R" 0.13333333333333333, "5) Lean D" 0.06666666666666667, "7) Solid D" 0.4, "6) Likely D" 0.06666666666666667}]
                    "percent_single_parent" [0.1317478495540774, 0.007414670845695041]
                    "percent_owned" [0.5383343679535155, 0.04953863233628882]}
   0.0183908045977 {"percent_white" [0.27903254765215924, 0.05329598366668918]
                    "percent_lths" [0.23054245273415794, 0.04246666339612755]
                    "percent_children" [0.22581085315935995, 0.0158784002264082]
                    "percent_married" [0.35494936716533654, 0.02038185467517615]
                    "percent_poor" [0.18769577100094537, 0.024011182290757778]
                    "percent_manufacturing" [0.05577853033389426, 0.028208838929491588]
                    "alex_type" [{"rural" 0.2, "bipolar" 0.2, "western_ag" 0.2, "center_right" 0.2, "diverse" 0.2}]
                    "cluster_name" [{"Rural populists" 0.16666666666666666, "Romney Republicans" 0.16666666666666666, "White moderates" 0.16666666666666666, "Diverse, educated suburbs" 0.16666666666666666, "Loyal GOP suburbs" 0.16666666666666666, "Emerging resistance" 0.16666666666666666}]
                    "nyt_rating" [{"3) Lean R" 0.06666666666666667, "4) Toss Up" 0.06666666666666667, "1) Solid R" 0.06666666666666667, "2) Likely R" 0.06666666666666667, "5) Lean D" 0.06666666666666667, "7) Solid D" 0.6, "6) Likely D" 0.06666666666666667}]
                    "percent_single_parent" [0.1275766743272279, 0.013834738884417008]
                    "percent_owned" [0.3529461534787477, 0.06471189998756025]}
   0.00229885057471 {"percent_white" [0.9800703661276746, 0.0001]
                     "percent_lths" [0.23387601519117313, 0.0001]
                     "percent_children" [0.2633754270163561, 0.0001]
                     "percent_married" [0.38018084172308164, 0.0001]
                     "percent_poor" [0.28925464047785915, 0.0001]
                     "percent_manufacturing" [0.0924261346361814, 0.0001]
                     "alex_type" [{"rural" 0.2, "bipolar" 0.2, "western_ag" 0.2, "center_right" 0.2, "diverse" 0.2}]
                     "cluster_name" [{"Rural populists" 0.16666666666666666, "Romney Republicans" 0.16666666666666666, "White moderates" 0.16666666666666666, "Diverse, educated suburbs" 0.16666666666666666, "Loyal GOP suburbs" 0.16666666666666666, "Emerging resistance" 0.16666666666666666}]
                     "nyt_rating" [{"3) Lean R" 0.125, "4) Toss Up" 0.125, "1) Solid R" 0.25, "2) Likely R" 0.125, "5) Lean D" 0.125, "7) Solid D" 0.125, "6) Likely D" 0.125}]
                     "percent_single_parent" [0.12605980108860307, 0.0001]
                     "percent_owned" [0.6212654597629552, 0.0001]}
   0.00459770114943 {"percent_white" [0.13002968239023305, 0.019549999999999998]
                     "percent_lths" [0.2661730275298622, 0.04224999999999998]
                     "percent_children" [0.2634860880784727, 0.0036500000000000005]
                     "percent_married" [0.27750628090888185, 0.02780000000000002]
                     "percent_poor" [0.25948657207255454, 0.013800000000000007]
                     "percent_manufacturing" [0.06046026782723295, 0.0339]
                     "alex_type" [{"rural" 0.2, "bipolar" 0.2, "western_ag" 0.2, "center_right" 0.2, "diverse" 0.2}]
                     "cluster_name" [{"Rural populists" 0.16666666666666666, "Romney Republicans" 0.16666666666666666, "White moderates" 0.16666666666666666, "Diverse, educated suburbs" 0.16666666666666666, "Loyal GOP suburbs" 0.16666666666666666, "Emerging resistance" 0.16666666666666666}]
                     "nyt_rating" [{"3) Lean R" 0.1111111111111111, "4) Toss Up" 0.1111111111111111, "1) Solid R" 0.1111111111111111, "2) Likely R" 0.1111111111111111, "5) Lean D" 0.1111111111111111, "7) Solid D" 0.3333333333333333, "6) Likely D" 0.1111111111111111}]
                     "percent_single_parent" [0.15071550921377747, 0.019850000000000007]
                     "percent_owned" [0.2047415292742671, 0.0555]}])

(def generate-census-row
  (multi-mixture
   (view
    {"percent_white"         dist/gaussian
     "percent_lths"          dist/gaussian
     "percent_children"      dist/gaussian
     "percent_married"       dist/gaussian
     "percent_poor"          dist/gaussian
     "percent_manufacturing" dist/gaussian
     "alex_type"             dist/categorical
     "cluster_name"          dist/categorical
     "nyt_rating"            dist/categorical
     "percent_single_parent" dist/gaussian
     "percent_owned"         dist/gaussian}
    (apply clusters cluster-data))))

(defn make-identity-output-addr-map
  [output-addrs-types]
  (let [output-addrs (keys output-addrs-types)
        trace-addrs  (map clojure.core/name output-addrs)]
    (clojure.core/zipmap output-addrs trace-addrs)))

(def census-cgpm
  (let [table-variables  {:percent_white         cgpm/real-type
                          :percent_lths          cgpm/real-type
                          :percent_children      cgpm/real-type
                          :percent_married       cgpm/real-type
                          :percent_poor          cgpm/real-type
                          :percent_manufacturing cgpm/real-type
                          :alex_type             (cgpm/make-nominal-type #{"western_ag" "diverse" "bipolar" "center_right" "rural"})
                          :cluster_name          (cgpm/make-nominal-type #{"Emerging resistance" "Loyal GOP suburbs" "Diverse, educated suburbs" "Romney Republicans" "White moderates" "Rural populists"})
                          :nyt_rating            (cgpm/make-nominal-type #{"1) Solid R" "7) Solid D" "6) Likely D" "5) Lean D" "2) Likely R" "3) Lean R" "4) Toss Up"})
                          :percent_single_parent cgpm/real-type
                          :percent_owned         cgpm/real-type}
        latent-variables {:cluster-for-percent_white         cgpm/integer-type
                          :cluster-for-percent_lths          cgpm/integer-type
                          :cluster-for-percent_children      cgpm/integer-type
                          :cluster-for-percent_married       cgpm/integer-type
                          :cluster-for-percent_poor          cgpm/integer-type
                          :cluster-for-percent_manufacturing cgpm/integer-type
                          :cluster-for-alex_type             cgpm/integer-type
                          :cluster-for-cluster_name          cgpm/integer-type
                          :cluster-for-nyt_rating            cgpm/integer-type
                          :cluster-for-percent_single_parent cgpm/integer-type
                          :cluster-for-percent_owned         cgpm/integer-type}

        outputs-addrs-types (into table-variables latent-variables)
        output-addr-map     (make-identity-output-addr-map outputs-addrs-types)
        inputs-addrs-types  {}
        input-addr-map      {}]

    (cgpm/make-cgpm generate-census-row
                    outputs-addrs-types
                    inputs-addrs-types
                    output-addr-map
                    input-addr-map)))
