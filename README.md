alexarankrider
==============

Read global rank from http://www.alexa.com and put it in to the file in json format.

The project is in the eclipse format

Main features
-------------

Alexa is a good site to check you rank, unfortunatly it's does not store statistic for the sites that have less than 100,000 rank. 

You could buy account, for store such statistic for you own site.

Sometime you need to look for the statistic of your competitors, that are lower than 100,000. 

This program allow you to gather statistic for any site that is on the www.alexa.com.

You need to put URL in to the properties file, run program and look in to the resulting json. 

input example

    url.1=http://www.alexa.com/siteinfo/championselect.net
    url.2=http://www.alexa.com/siteinfo/elohell.net
    url.3=http://www.alexa.com/siteinfo/lolcounter.com
    url.4=http://www.alexa.com/siteinfo/lolcounterpicks.com
    url.5=http://www.alexa.com/siteinfo/counterlol.org
    url.6=http://www.alexa.com/siteinfo/picklol.net
    url.7=http://www.alexa.com/siteinfo/counterpicker.com
    
result example

    {
      "http://www.alexa.com/siteinfo/championselect.net" : "10,546",
      "http://www.alexa.com/siteinfo/elohell.net" : "16,323",
      "http://www.alexa.com/siteinfo/lolcounter.com" : "16,487",
      "http://www.alexa.com/siteinfo/lolcounterpicks.com" : "462,863",
      "http://www.alexa.com/siteinfo/counterlol.org" : "4,722,060",
      "http://www.alexa.com/siteinfo/picklol.net" : "7,679,714",
      "http://www.alexa.com/siteinfo/counterpicker.com" : "12,461,834"
    }
