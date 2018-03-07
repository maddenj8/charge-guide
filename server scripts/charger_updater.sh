while true
DATE_WITH_TIME=`date "+%Y%m%d-%H%M%S"`
do
	if [ "$1" == "history" ]; then
		DATE_WITH_TIME="$DATE_WITH_TIME-charging-location.kml"
		wget -O - q http://esb.ie/electric-cars/kml/charging-locations.kml > ./historic_data/$DATE_WITH_TIME
		sleep 600
	else
		wget -O - q http://esb.ie/electric-cars/kml/charging-locations.kml > charging-locations.kml
		nice python3 split.py
		nice python3 parse.py
		sleep 600
	fi
done

