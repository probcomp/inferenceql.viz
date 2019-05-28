(ns inferdb.spreadsheets.model
  (:require [inferdb.multimixture.dsl :refer [multi-mixture view clusters]]
            [inferdb.cgpm.main :as cgpm]
            [metaprob.distributions :as dist]))

(def cluster-data
  [0.296551724138 {"percent_white" [0.8366130786500527, 0.07068988790759735]
                   "percent_children" [0.21827696347658487, 0.0198316836638368]
                   "percent_married" [0.5122359841852, 0.021414205402424967]
                   "percent_poor" [0.12632146950738188, 0.02692248428073017]
                   "nyt_classification_1" [{"rural" 0.35, "bipolar" 0.25, "western_ag" 0.05, "center_right" 0.3, "diverse" 0.05}]
                   "percent_manufacturing" [0.1335790490288156, 0.05204879401211422]
                   "nyt_political_prediction" [{"5) Lean D" 0.02608695652173913, "4) Toss Up" 0.06956521739130435, "1) Solid R" 0.6521739130434783, "2) Likely R" 0.09565217391304348, "3) Lean R" 0.06086956521739131, "7) Solid D" 0.0782608695652174, "6) Likely D" 0.017391304347826087}]
                   "percent_single_parent" [0.09182979124504345, 0.011635331169949954]
                   "nyt_classification_2" [{"Rural populists" 0.32, "Romney Republicans" 0.04, "White moderates" 0.32, "Diverse, educated suburbs" 0.04, "Loyal GOP suburbs" 0.24, "Emerging resistance" 0.04}]
                   "percent_owned" [0.7113674988579285, 0.03639860638763965]}
   0.0436781609195 {"percent_white" [0.37841035938701906, 0.11247251975852157]
                    "percent_children" [0.23436996419420014, 0.014433252618984913]
                    "percent_married" [0.35524480811153053, 0.02892215456930462]
                    "percent_poor" [0.2398173601784906, 0.028355467211379593]
                    "nyt_classification_1" [{"rural" 0.2, "bipolar" 0.2, "western_ag" 0.2, "center_right" 0.2, "diverse" 0.2}]
                    "percent_manufacturing" [0.10826795128155423, 0.03207865161126128]
                    "nyt_political_prediction" [{"5) Lean D" 0.041666666666666664, "4) Toss Up" 0.041666666666666664, "1) Solid R" 0.08333333333333333, "2) Likely R" 0.041666666666666664, "3) Lean R" 0.041666666666666664, "7) Solid D" 0.7083333333333334, "6) Likely D" 0.041666666666666664}]
                    "percent_single_parent" [0.16171741037076276, 0.012880144267184677]
                    "nyt_classification_2" [{"Rural populists" 0.16666666666666666, "Romney Republicans" 0.16666666666666666, "White moderates" 0.16666666666666666, "Diverse, educated suburbs" 0.16666666666666666, "Loyal GOP suburbs" 0.16666666666666666, "Emerging resistance" 0.16666666666666666}]
                    "percent_owned" [0.5274021988266426, 0.05373744471026623]}
   0.0758620689655 {"percent_white" [0.37345272679137015, 0.0892579477160812]
                    "percent_children" [0.24215982606983133, 0.020540621582275112]
                    "percent_married" [0.44395175939087694, 0.03528723798161288]
                    "percent_poor" [0.15902417805777108, 0.03382317900518118]
                    "nyt_classification_1" [{"rural" 0.1111111111111111, "bipolar" 0.1111111111111111, "western_ag" 0.5555555555555556, "center_right" 0.1111111111111111, "diverse" 0.1111111111111111}]
                    "percent_manufacturing" [0.06603287926532558, 0.027718782936059477]
                    "nyt_political_prediction" [{"5) Lean D" 0.03125, "4) Toss Up" 0.09375, "1) Solid R" 0.0625, "2) Likely R" 0.0625, "3) Lean R" 0.0625, "7) Solid D" 0.59375, "6) Likely D" 0.09375}]
                    "percent_single_parent" [0.12954183935474628, 0.011585379152233653]
                    "nyt_classification_2" [{"Rural populists" 0.1, "Romney Republicans" 0.1, "White moderates" 0.1, "Diverse, educated suburbs" 0.1, "Loyal GOP suburbs" 0.1, "Emerging resistance" 0.5}]
                    "percent_owned" [0.5841028356293197, 0.06589887109898934]}
   0.112643678161 {"percent_white" [0.6490025048698238, 0.07461520636734277]
                   "percent_children" [0.2340409450349926, 0.01447380606673573]
                   "percent_married" [0.47461107188785273, 0.021814196837689658]
                   "percent_poor" [0.16551457263391287, 0.026352670775013676]
                   "nyt_classification_1" [{"rural" 0.09090909090909091, "bipolar" 0.2727272727272727, "western_ag" 0.09090909090909091, "center_right" 0.45454545454545453, "diverse" 0.09090909090909091}]
                   "percent_manufacturing" [0.11959218401481284, 0.0382296365995756]
                   "nyt_political_prediction" [{"5) Lean D" 0.02040816326530612, "4) Toss Up" 0.061224489795918366, "1) Solid R" 0.7142857142857143, "2) Likely R" 0.04081632653061224, "3) Lean R" 0.061224489795918366, "7) Solid D" 0.08163265306122448, "6) Likely D" 0.02040816326530612}]
                   "percent_single_parent" [0.12046249236064697, 0.009037134275773894]
                   "nyt_classification_2" [{"Rural populists" 0.15384615384615385, "Romney Republicans" 0.07692307692307693, "White moderates" 0.15384615384615385, "Diverse, educated suburbs" 0.23076923076923078, "Loyal GOP suburbs" 0.3076923076923077, "Emerging resistance" 0.07692307692307693}]
                   "percent_owned" [0.6687052506519325, 0.039366408723387]}
   0.0758620689655 {"percent_white" [0.7004202676141043, 0.0784577457591355]
                    "percent_children" [0.2047224962701087, 0.011965948012829157]
                    "percent_married" [0.44322389219260827, 0.024163560358926153]
                    "percent_poor" [0.14632875630997444, 0.024457152052137582]
                    "nyt_classification_1" [{"rural" 0.125, "bipolar" 0.25, "western_ag" 0.125, "center_right" 0.25, "diverse" 0.25}]
                    "percent_manufacturing" [0.0923896132241886, 0.027231098869458063]
                    "nyt_political_prediction" [{"5) Lean D" 0.05263157894736842, "4) Toss Up" 0.02631578947368421, "1) Solid R" 0.10526315789473684, "2) Likely R" 0.07894736842105263, "3) Lean R" 0.02631578947368421, "7) Solid D" 0.631578947368421, "6) Likely D" 0.07894736842105263}]
                    "percent_single_parent" [0.10252498035434848, 0.01206869304020971]
                    "nyt_classification_2" [{"Rural populists" 0.2, "Romney Republicans" 0.1, "White moderates" 0.3, "Diverse, educated suburbs" 0.1, "Loyal GOP suburbs" 0.1, "Emerging resistance" 0.2}]
                    "percent_owned" [0.6232968431216583, 0.05444238595460114]}
   0.0735632183908 {"percent_white" [0.7255854439670023, 0.09734589403738352]
                    "percent_children" [0.25490363411052974, 0.028531331462096188]
                    "percent_married" [0.5768545674950454, 0.02025971892543182]
                    "percent_poor" [0.07091127671854636, 0.01725026720807536]
                    "nyt_classification_1" [{"rural" 0.058823529411764705, "bipolar" 0.058823529411764705, "western_ag" 0.058823529411764705, "center_right" 0.7058823529411765, "diverse" 0.11764705882352941}]
                    "percent_manufacturing" [0.10194396509008377, 0.03532008989229784]
                    "nyt_political_prediction" [{"5) Lean D" 0.08823529411764706, "4) Toss Up" 0.23529411764705882, "1) Solid R" 0.29411764705882354, "2) Likely R" 0.14705882352941177, "3) Lean R" 0.058823529411764705, "7) Solid D" 0.11764705882352941, "6) Likely D" 0.058823529411764705}]
                    "percent_single_parent" [0.07904610745463206, 0.015406230666823569]
                    "nyt_classification_2" [{"Rural populists" 0.05263157894736842, "Romney Republicans" 0.5789473684210527, "White moderates" 0.05263157894736842, "Diverse, educated suburbs" 0.05263157894736842, "Loyal GOP suburbs" 0.21052631578947367, "Emerging resistance" 0.05263157894736842}]
                    "percent_owned" [0.7248401510467484, 0.03865245585664642]}
   0.016091954023 {"percent_white" [0.6430510843112773, 0.0860533033672047]
                   "percent_children" [0.16087111092567838, 0.025111920902801753]
                   "percent_married" [0.4449958269678367, 0.033453964714246366]
                   "percent_poor" [0.10478842108790984, 0.028221109482232682]
                   "nyt_classification_1" [{"rural" 0.2, "bipolar" 0.2, "western_ag" 0.2, "center_right" 0.2, "diverse" 0.2}]
                   "percent_manufacturing" [0.06075150002195906, 0.017768568901752515]
                   "nyt_political_prediction" [{"5) Lean D" 0.07692307692307693, "4) Toss Up" 0.07692307692307693, "1) Solid R" 0.07692307692307693, "2) Likely R" 0.07692307692307693, "3) Lean R" 0.07692307692307693, "7) Solid D" 0.5384615384615384, "6) Likely D" 0.07692307692307693}]
                   "percent_single_parent" [0.04411693260009422, 0.008306918680183626]
                   "nyt_classification_2" [{"Rural populists" 0.16666666666666666, "Romney Republicans" 0.16666666666666666, "White moderates" 0.16666666666666666, "Diverse, educated suburbs" 0.16666666666666666, "Loyal GOP suburbs" 0.16666666666666666, "Emerging resistance" 0.16666666666666666}]
                   "percent_owned" [0.4074058731737442, 0.10288160820147813]}
   0.0114942528736 {"percent_white" [0.14200685004156133, 0.024453907663193625]
                    "percent_children" [0.2826642220148612, 0.008632867426295864]
                    "percent_married" [0.46766850270943394, 0.012816770263993981]
                    "percent_poor" [0.2638009245906493, 0.01644355192773143]
                    "nyt_classification_1" [{"rural" 0.16666666666666666, "bipolar" 0.16666666666666666, "western_ag" 0.3333333333333333, "center_right" 0.16666666666666666, "diverse" 0.16666666666666666}]
                    "percent_manufacturing" [0.07099428834135338, 0.023275704070983547]
                    "nyt_political_prediction" [{"5) Lean D" 0.08333333333333333, "4) Toss Up" 0.08333333333333333, "1) Solid R" 0.08333333333333333, "2) Likely R" 0.16666666666666666, "3) Lean R" 0.08333333333333333, "7) Solid D" 0.4166666666666667, "6) Likely D" 0.08333333333333333}]
                    "percent_single_parent" [0.1713406592816673, 0.006818973529791707]
                    "nyt_classification_2" [{"Rural populists" 0.14285714285714285, "Romney Republicans" 0.14285714285714285, "White moderates" 0.14285714285714285, "Diverse, educated suburbs" 0.14285714285714285, "Loyal GOP suburbs" 0.14285714285714285, "Emerging resistance" 0.2857142857142857}]
                    "percent_owned" [0.6282827846791779, 0.08422523612314779]}
   0.0459770114943 {"percent_white" [0.18319732629419339, 0.052520952009650394]
                    "percent_children" [0.2541363136567578, 0.016237099494675767]
                    "percent_married" [0.40588626839421355, 0.023371572475980294]
                    "percent_poor" [0.17493827966118064, 0.023781641553938197]
                    "nyt_classification_1" [{"rural" 0.16666666666666666, "bipolar" 0.16666666666666666, "western_ag" 0.16666666666666666, "center_right" 0.16666666666666666, "diverse" 0.3333333333333333}]
                    "percent_manufacturing" [0.07848552090318771, 0.030993371872063222]
                    "nyt_political_prediction" [{"5) Lean D" 0.038461538461538464, "4) Toss Up" 0.07692307692307693, "1) Solid R" 0.038461538461538464, "2) Likely R" 0.038461538461538464, "3) Lean R" 0.038461538461538464, "7) Solid D" 0.7307692307692307, "6) Likely D" 0.038461538461538464}]
                    "percent_single_parent" [0.15937651615581255, 0.01362837481139993]
                    "nyt_classification_2" [{"Rural populists" 0.14285714285714285, "Romney Republicans" 0.14285714285714285, "White moderates" 0.14285714285714285, "Diverse, educated suburbs" 0.14285714285714285, "Loyal GOP suburbs" 0.14285714285714285, "Emerging resistance" 0.2857142857142857}]
                    "percent_owned" [0.5061642802345088, 0.07624476031177486]}
   0.0137931034483 {"percent_white" [0.11634775520299387, 0.06575592917921716]
                    "percent_children" [0.2891889781598722, 0.015797221978000514]
                    "percent_married" [0.4103913916691433, 0.019320232630299475]
                    "percent_poor" [0.24850101246055517, 0.02780825716701187]
                    "nyt_classification_1" [{"rural" 0.2, "bipolar" 0.2, "western_ag" 0.2, "center_right" 0.2, "diverse" 0.2}]
                    "percent_manufacturing" [0.10540525161032009, 0.029959403087066558]
                    "nyt_political_prediction" [{"5) Lean D" 0.08333333333333333, "4) Toss Up" 0.08333333333333333, "1) Solid R" 0.08333333333333333, "2) Likely R" 0.08333333333333333, "3) Lean R" 0.08333333333333333, "7) Solid D" 0.5, "6) Likely D" 0.08333333333333333}]
                    "percent_single_parent" [0.20795500878373246, 0.015581978550734676]
                    "nyt_classification_2" [{"Rural populists" 0.16666666666666666, "Romney Republicans" 0.16666666666666666, "White moderates" 0.16666666666666666, "Diverse, educated suburbs" 0.16666666666666666, "Loyal GOP suburbs" 0.16666666666666666, "Emerging resistance" 0.16666666666666666}]
                    "percent_owned" [0.4165584018551043, 0.05166320418505484]}
   0.032183908046 {"percent_white" [0.266541827327448, 0.0796239721363831]
                   "percent_children" [0.21546534296607156, 0.017074363166777858]
                   "percent_married" [0.34023072867387494, 0.048868148823009]
                   "percent_poor" [0.22451162578936562, 0.033450505624438805]
                   "nyt_classification_1" [{"rural" 0.2, "bipolar" 0.2, "western_ag" 0.2, "center_right" 0.2, "diverse" 0.2}]
                   "percent_manufacturing" [0.0547855373259348, 0.02071723254901088]
                   "nyt_political_prediction" [{"5) Lean D" 0.047619047619047616, "4) Toss Up" 0.047619047619047616, "1) Solid R" 0.047619047619047616, "2) Likely R" 0.047619047619047616, "3) Lean R" 0.047619047619047616, "7) Solid D" 0.7142857142857143, "6) Likely D" 0.047619047619047616}]
                   "percent_single_parent" [0.1383166341658812, 0.01882373122265767]
                   "nyt_classification_2" [{"Rural populists" 0.16666666666666666, "Romney Republicans" 0.16666666666666666, "White moderates" 0.16666666666666666, "Diverse, educated suburbs" 0.16666666666666666, "Loyal GOP suburbs" 0.16666666666666666, "Emerging resistance" 0.16666666666666666}]
                   "percent_owned" [0.2889688721441883, 0.1013018385815381]}
   0.00229885057471 {"percent_white" [0.06481800824590987, 0.0001]
                     "percent_children" [0.2584688356076461, 0.0001]
                     "percent_married" [0.32519904317061754, 0.0001]
                     "percent_poor" [0.3439956592671287, 0.0001]
                     "nyt_classification_1" [{"rural" 0.2, "bipolar" 0.2, "western_ag" 0.2, "center_right" 0.2, "diverse" 0.2}]
                     "percent_manufacturing" [0.036394140419521326, 0.0001]
                     "nyt_political_prediction" [{"5) Lean D" 0.14285714285714285, "4) Toss Up" 0.14285714285714285, "1) Solid R" 0.14285714285714285, "2) Likely R" 0.14285714285714285, "3) Lean R" 0.14285714285714285, "7) Solid D" 0.14285714285714285, "6) Likely D" 0.14285714285714285}]
                     "percent_single_parent" [0.26833484904734456, 0.0001]
                     "nyt_classification_2" [{"Rural populists" 0.16666666666666666, "Romney Republicans" 0.16666666666666666, "White moderates" 0.16666666666666666, "Diverse, educated suburbs" 0.16666666666666666, "Loyal GOP suburbs" 0.16666666666666666, "Emerging resistance" 0.16666666666666666}]
                     "percent_owned" [0.21899594403785685, 0.0001]}
   0.179310344828 {"percent_white" [0.5808022556670032, 0.11288071392812447]
                   "percent_children" [0.22599095662060412, 0.021335576959188605]
                   "percent_married" [0.5013611883085025, 0.02580125134573742]
                   "percent_poor" [0.10242178573970971, 0.017359611964202024]
                   "nyt_classification_1" [{"rural" 0.058823529411764705, "bipolar" 0.17647058823529413, "western_ag" 0.058823529411764705, "center_right" 0.23529411764705882, "diverse" 0.47058823529411764}]
                   "percent_manufacturing" [0.08127213199763755, 0.032575737170515394]
                   "nyt_political_prediction" [{"5) Lean D" 0.02531645569620253, "4) Toss Up" 0.08860759493670886, "1) Solid R" 0.1518987341772152, "2) Likely R" 0.12658227848101267, "3) Lean R" 0.06329113924050633, "7) Solid D" 0.5063291139240507, "6) Likely D" 0.0379746835443038}]
                   "percent_single_parent" [0.09338722316648404, 0.01472641948901597]
                   "nyt_classification_2" [{"Rural populists" 0.05, "Romney Republicans" 0.25, "White moderates" 0.05, "Diverse, educated suburbs" 0.35, "Loyal GOP suburbs" 0.05, "Emerging resistance" 0.25}]
                   "percent_owned" [0.6355215107669074, 0.058613557860131726]}
   0.016091954023 {"percent_white" [0.27555004252195303, 0.08160361386555358]
                   "percent_children" [0.18255826780448492, 0.011537728705142513]
                   "percent_married" [0.48050223450678475, 0.05696732683328761]
                   "percent_poor" [0.12831682809083722, 0.031880734378144364]
                   "nyt_classification_1" [{"rural" 0.16666666666666666, "bipolar" 0.16666666666666666, "western_ag" 0.16666666666666666, "center_right" 0.16666666666666666, "diverse" 0.3333333333333333}]
                   "percent_manufacturing" [0.0654726741039284, 0.04898541223920543]
                   "nyt_political_prediction" [{"5) Lean D" 0.16666666666666666, "4) Toss Up" 0.08333333333333333, "1) Solid R" 0.08333333333333333, "2) Likely R" 0.08333333333333333, "3) Lean R" 0.08333333333333333, "7) Solid D" 0.4166666666666667, "6) Likely D" 0.08333333333333333}]
                   "percent_single_parent" [0.07739183983436751, 0.01441660040879745]
                   "nyt_classification_2" [{"Rural populists" 0.14285714285714285, "Romney Republicans" 0.14285714285714285, "White moderates" 0.14285714285714285, "Diverse, educated suburbs" 0.14285714285714285, "Loyal GOP suburbs" 0.14285714285714285, "Emerging resistance" 0.2857142857142857}]
                   "percent_owned" [0.4584977047197453, 0.03749132144474863]}
   0.00459770114943 {"percent_white" [0.9258315536813309, 0.015100000000000002]
                     "percent_children" [0.18200589611523216, 0.008050000000000002]
                     "percent_married" [0.5270631514593547, 0.0045499999999999985]
                     "percent_poor" [0.27491311831699183, 0.04135000000000001]
                     "nyt_classification_1" [{"rural" 0.3333333333333333, "bipolar" 0.16666666666666666, "western_ag" 0.16666666666666666, "center_right" 0.16666666666666666, "diverse" 0.16666666666666666}]
                     "percent_manufacturing" [0.08020251767120866, 0.027500000000000004]
                     "nyt_political_prediction" [{"5) Lean D" 0.1111111111111111, "4) Toss Up" 0.1111111111111111, "1) Solid R" 0.2222222222222222, "2) Likely R" 0.1111111111111111, "3) Lean R" 0.2222222222222222, "7) Solid D" 0.1111111111111111, "6) Likely D" 0.1111111111111111}]
                     "percent_single_parent" [0.1181243480950551, 0.007550000000000001]
                     "nyt_classification_2" [{"Rural populists" 0.2857142857142857, "Romney Republicans" 0.14285714285714285, "White moderates" 0.14285714285714285, "Diverse, educated suburbs" 0.14285714285714285, "Loyal GOP suburbs" 0.14285714285714285, "Emerging resistance" 0.14285714285714285}]
                     "percent_owned" [0.725018805792641, 0.00020000000000003348]}])

(def stattypes
  {"percent_white"            dist/gaussian
   "percent_children"         dist/gaussian
   "percent_married"          dist/gaussian
   "percent_poor"             dist/gaussian
   "percent_manufacturing"    dist/gaussian
   "percent_single_parent"    dist/gaussian
   "nyt_political_prediction" dist/categorical
   "nyt_classification_1"     dist/categorical
   "nyt_classification_2"     dist/categorical
   "percent_owned"            dist/gaussian})

(def generate-census-row
  (multi-mixture
   (view stattypes (apply clusters cluster-data))))

(defn make-identity-output-addr-map
  [output-addrs-types]
  (let [output-addrs (keys output-addrs-types)
        trace-addrs  (map clojure.core/name output-addrs)]
    (clojure.core/zipmap output-addrs trace-addrs)))

(def census-cgpm
  (let [table-variables  {:percent_white            cgpm/real-type
                          :percent_children         cgpm/real-type
                          :percent_married          cgpm/real-type
                          :percent_poor             cgpm/real-type
                          :percent_manufacturing    cgpm/real-type
                          :nyt_classification_1     (cgpm/make-nominal-type #{"western_ag" "diverse" "bipolar" "center_right" "rural"})
                          :nyt_political_prediction (cgpm/make-nominal-type #{"1) Solid R" "7) Solid D" "6) Likely D" "5) Lean D" "2) Likely R" "4) Toss Up" "3) Lean R"})
                          :percent_single_parent    cgpm/real-type
                          :nyt_classification_2     (cgpm/make-nominal-type #{"Emerging resistance" "Loyal GOP suburbs" "Diverse, educated suburbs" "Romney Republicans" "White moderates" "Rural populists"})
                          :percent_owned            cgpm/real-type}
        latent-variables {:cluster-for-percent_white            cgpm/integer-type
                          :cluster-for-percent_children         cgpm/integer-type
                          :cluster-for-percent_married          cgpm/integer-type
                          :cluster-for-percent_poor             cgpm/integer-type
                          :cluster-for-percent_manufacturing    cgpm/integer-type
                          :cluster-for-nyt_classification_1     cgpm/integer-type
                          :cluster-for-nyt_political_prediction cgpm/integer-type
                          :cluster-for-percent_single_parent    cgpm/integer-type
                          :cluster-for-nyt_classification_2     cgpm/integer-type
                          :cluster-for-percent_owned            cgpm/integer-type}

        outputs-addrs-types (into table-variables latent-variables)
        output-addr-map     (make-identity-output-addr-map outputs-addrs-types)
        inputs-addrs-types  {}
        input-addr-map      {}]

    (cgpm/make-cgpm generate-census-row
                    outputs-addrs-types
                    inputs-addrs-types
                    output-addr-map
                    input-addr-map)))
