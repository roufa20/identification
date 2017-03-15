# FingerPrint



## Synopsis

This Folder contain the FingerPrint Spark Job

## Requirement

scalaVersion 2.11.6

## Installation

Provide code examples and explanations of how to get the project.

## Test in SBT

```
sbt "run localhost neo4j admin DirectKafkaWordCount spark.master local[*] bigdata2.ip-188-165-248.eu:9092,bigdata3.ip-188-165-248.eu:9092,bigdata4.ip-188-165-248.eu:9092 enrich_selcareb2b identifiedSelcareb2b NonIdentifiedSelcareb2b true"
```

## Tests in SPARK SUBMIT

```
spark-submit "JAR_PATH" localhost neo4j admin DirectKafkaWordCount spark.master local[*] bigdata2.ip-188-165-248.eu:9092,bigdata3.ip-188-165-248.eu:9092,bigdata4.ip-188-165-248.eu:9092 enrich_selcareb2b identifiedSelcareb2b NonIdentifiedSelcareb2b true
```

*localhost: neo4j Host

*neo4j : neo4j login

*admin : neo4j password

*bigdata2.ip-188-165-248.eu:9092,bigdata3.ip-188-165-248.eu:9092,bigdata4.ip-188-165-248.eu:9092 : broker list

*enrich_selcareb2b : enrichement topic

*identifiedSelcareb2b : consumer topic for identified User

*NonIdentifiedSelcareb2b : consumer Topic for Unidentified User

* true : if you want to read from beginning, if not Pleas Make It false


## CSV line

```
app_id"\t"platform"\t"etl_tstamp"\t"collector_tstamp"\t"dvce_created_tstamp"\t"event"\t"event_id"\t"txn_id"\t"name_tracker"\t"v_tracker"\t"v_collector"\t"v_etl"\t"user_id"\t"user_ipaddress"\t"user_fingerprint"\t"domain_userid"\t"domain_sessionidx"\t"network_userid"\t"geo_country"\t"geo_region"\t"geo_city"\t"geo_zipcode"\t"geo_latitude"\t"geo_longitude"\t"geo_region_name"\t"ip_isp"\t"ip_organization"\t"ip_domain"\t"ip_netspeed"\t"page_url"\t"page_title"\t"page_referrer"\t"page_urlscheme"\t"page_urlhost"\t"page_urlport"\t"page_urlpath"\t"page_urlquery"\t"page_urlfragment"\t"refr_urlscheme"\t"refr_urlhost"\t"refr_urlport"\t"refr_urlpath"\t"refr_urlquery"\t"refr_urlfragment"\t"refr_medium"\t"refr_source"\t"refr_term"\t"mkt_medium"\t"mkt_source"\t"mkt_term"\t"mkt_content"\t"mkt_campaign"\t"contexts_org_w3_performance_timing_1/0/chromeFirstPaint"\t"contexts_org_w3_performance_timing_1/0/loadEventEnd"\t"contexts_org_w3_performance_timing_1/0/loadEventStart"\t"contexts_org_w3_performance_timing_1/0/domComplete"\t"contexts_org_w3_performance_timing_1/0/domContentLoadedEventEnd"\t"contexts_org_w3_performance_timing_1/0/domContentLoadedEventStart"\t"contexts_org_w3_performance_timing_1/0/domInteractive"\t"contexts_org_w3_performance_timing_1/0/domLoading"\t"contexts_org_w3_performance_timing_1/0/responseEnd"\t"contexts_org_w3_performance_timing_1/0/responseStart"\t"contexts_org_w3_performance_timing_1/0/requestStart"\t"contexts_org_w3_performance_timing_1/0/secureConnectionStart"\t"contexts_org_w3_performance_timing_1/0/connectEnd"\t"contexts_org_w3_performance_timing_1/0/connectStart"\t"contexts_org_w3_performance_timing_1/0/domainLookupEnd"\t"contexts_org_w3_performance_timing_1/0/domainLookupStart"\t"contexts_org_w3_performance_timing_1/0/fetchStart"\t"contexts_org_w3_performance_timing_1/0/redirectEnd"\t"contexts_org_w3_performance_timing_1/0/redirectStart"\t"contexts_org_w3_performance_timing_1/0/unloadEventEnd"\t"contexts_org_w3_performance_timing_1/0/unloadEventStart"\t"contexts_org_w3_performance_timing_1/0/navigationStart"\t"contexts_com_snowplowanalytics_snowplow_web_page_1/0/id"\t"se_category"\t"se_action"\t"se_label"\t"se_property"\t"se_value"\t"unstruct_event"\t"tr_orderid"\t"tr_affiliation"\t"tr_total"\t"tr_tax"\t"tr_shipping"\t"tr_city"\t"tr_state"\t"tr_country"\t"ti_orderid"\t"ti_sku"\t"ti_name"\t"ti_category"\t"ti_price"\t"ti_quantity"\t"pp_xoffset_min"\t"pp_xoffset_max"\t"pp_yoffset_min"\t"pp_yoffset_max"\t"useragent"\t"br_name"\t"br_family"\t"br_version"\t"br_type"\t"br_renderengine"\t"br_lang"\t"br_features_pdf"\t"br_features_flash"\t"br_features_java"\t"br_features_director"\t"br_features_quicktime"\t"br_features_realplayer"\t"br_features_windowsmedia"\t"br_features_gears"\t"br_features_silverlight"\t"br_cookies"\t"br_colordepth"\t"br_viewwidth"\t"br_viewheight"\t"os_name"\t"os_family"\t"os_manufacturer"\t"os_timezone"\t"dvce_type"\t"dvce_ismobile"\t"dvce_screenwidth"\t"dvce_screenheight"\t"doc_charset"\t"doc_width"\t"doc_height"\t"tr_currency"\t"tr_total_base"\t"tr_tax_base"\t"tr_shipping_base"\t"ti_currency"\t"ti_price_base"\t"base_currency"\t"geo_timezone"\t"mkt_clickid"\t"mkt_network"\t"etl_tags"\t"dvce_sent_tstamp"\t"refr_domain_userid"\t"refr_device_tstamp"\t"derived_contexts"\t"domain_sessionid"\t"derived_tstamp"\t"event_vendor"\t"event_name"\t"event_format"\t"event_version"\t"event_fingerprint"\t"true_tstamp
```
