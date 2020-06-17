(ns cpmcdaniel.vinyl.records-test
  (:require [clojure.test :refer :all]
            [cpmcdaniel.vinyl.records :as sut]
            [clj-time.core :as t]))

(deftest get-delimiter-test
  (let [test-data
        [["pipe" sut/pipe-delimiter "Wilson | Fred| male |blue | 01/22/2004"]
         ["comma" sut/comma-delimiter "Wilson, Fred,male ,blue, 01/22/2004"]
         ["space" sut/space-delimiter "Wilson Fred male blue 01/22/2004"]]]
    (doseq [[test-name expected input-line] test-data]
      (testing test-name
        (is (= expected (sut/get-delimiter input-line)))))))

(deftest check-delimiter!-test
  (is (thrown? IllegalArgumentException
               (sut/check-delimiter! nil)))
  (doseq [delim (seq sut/delimiter?)]
    (is (= delim (sut/check-delimiter! delim)))))

(deftest parse-records-test
  (let [[fred jane] (sut/parse-records "Wilson|Fred|male|blue|01/22/2004\nSmith|Jane|female|purple|04/13/2000")]
    (is (= "Wilson" (:last-name fred)))
    (is (= "Smith" (:last-name jane)))))

(deftest parse-gender-test
    (let [test-data [["M" ["M" "m" "MALE" "Male" "male"]]
                     ["F" ["F" "f" "FEMALE" "Female" "female"]]]]
      (doseq [[expected inputs] test-data]
        (doseq [input inputs]
          (let [actual (sut/parse-gender input)]
            (is (= expected actual))))))
    (is (nil? (sut/parse-gender "what is this?"))))

(deftest parse-date-test
  (let [good-data [[(t/local-date 2000 4 13) ["04/13/2000"
                                             "20000413"
                                             "2000-04-13"
                                             "2000/04/13"]]]
        bad-data ["13 JAN 2000" "13-JAN-2000" "blah"]]
    (doseq [[expected inputs] good-data]
      (doseq [input inputs]
        (is (t/equal? expected (sut/parse-date input))
            (format "%s did not parse as expected" input))))
    (doseq [input bad-data]
      (is (thrown? IllegalArgumentException
                   (sut/parse-date input))))))

(deftest sort-test
  (let [fred (sut/->Record "Wilson" "Fred" "M" "blue" (sut/parse-date "20010304"))
        jane (sut/->Record "Smith" "Jane" "F" "purple" (sut/parse-date "20010223"))
        tom (sut/->Record "Apple" "Tom" "M" "green" (sut/parse-date "20010112"))
        tests [[sut/sort-by-gender [fred jane tom] [jane tom fred]]
               [sut/sort-by-dob [fred jane tom] [tom jane fred]]
               [sut/sort-by-last-name [tom fred jane] [fred jane tom]]]]
    (doseq [[sort-fn records expected] tests]
      (is (= expected (sort-fn records))
          (format "Sort failed for sort-fn: %s" sort-fn)))))

(deftest format-record-test
  (is (= "Wilson, Fred, M, blue, 03/24/2001"
         (sut/format-record
           (sut/->Record "Wilson" "Fred" "M" "blue" (sut/parse-date "20010324"))))))