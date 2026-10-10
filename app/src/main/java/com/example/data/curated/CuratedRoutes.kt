package com.example.data.curated

import com.example.data.model.RouteParking
import com.example.data.model.RouteStop
import com.example.data.model.RouteTips
import com.example.data.model.SafeRoutePlan

object CuratedRoutes {

    val routes = listOf(
        SafeRoutePlan(
            id = "chennai-bengaluru",
            title = "Chennai to Bengaluru",
            origin = "Adyar, Chennai",
            destination = "Indiranagar, Bengaluru",
            distanceApprox = "345 km (Divided 4/6-lane NH48 & ORR)",
            primaryRoads = listOf("Chennai Outer Ring Road", "NH48"),
            mapsUrl = "https://www.google.com/maps/dir/Adyar+Chennai/Vandalur+Outer+Ring+Road+Toll+Plaza+Chennai+ORR/Sriperumbudur+Toll+Plaza+NH48/Walajah+Toll+Plaza+NH48/Pallikonda+Toll+Plaza+NH48/Vaniyambadi+Toll+Plaza+NH48/Krishnagiri+Toll+Plaza+NH48/Attibele+Toll+Plaza+NH48/Indiranagar+Bengaluru/",
            stops = listOf(
                RouteStop("Vandalur Outer Ring Road Toll Plaza", "Chennai ORR"),
                RouteStop("Sriperumbudur Toll Plaza", "NH48"),
                RouteStop("Walajah Toll Plaza", "NH48"),
                RouteStop("Pallikonda Toll Plaza", "NH48"),
                RouteStop("Vaniyambadi Toll Plaza", "NH48"),
                RouteStop("Krishnagiri Toll Plaza", "NH48"),
                RouteStop("Attibele Toll Plaza", "NH48")
            ),
            verifyLine = "✅ Road numbers used: Chennai ORR, NH48. Final 2 km inside Bengaluru leaves NH48 via Old Airport Road.",
            tips = RouteTips(
                bestTime = "04:30 AM to 05:30 AM departure to clear Sriperumbudur before truck queues.",
                avoid = "Ranipet-Walajah stretch road widening diversions during dark hours (10:00 PM - 04:00 AM).",
                localNote = "Sriperumbudur to Walajah currently has elevated corridor construction; stay on main lane, avoid service road crater traps for low-profile tyres.",
                corridorNote = "Krishnagiri ghat section is divided 6-lane with speed barriers; keep low gear in i20 N Line manual/DCT to manage engine braking."
            ),
            parking = RouteParking(
                bestLotName = "Garuda Mall Car Parking",
                bestLotType = "Commercial / Covered",
                bestLotMapLink = "https://www.google.com/maps/search/Garuda+Mall+Car+Parking+Bengaluru/",
                backupLotName = "Indiranagar BMTC TTMC Multi-Level Car Parking",
                backupLotType = "Public Multi-Level",
                backupLotMapLink = "https://www.google.com/maps/search/Indiranagar+TTMC+Parking+Bengaluru/"
            )
        ),
        SafeRoutePlan(
            id = "mumbai-goa",
            title = "Mumbai to Goa (Divided Expressway & NH48)",
            origin = "Bandra West, Mumbai",
            destination = "Panaji, Goa",
            distanceApprox = "585 km (Divided Mumbai-Pune E-way, NH48 & NH748)",
            primaryRoads = listOf("Mumbai Pune Expressway", "NH48", "NH748"),
            mapsUrl = "https://www.google.com/maps/dir/Bandra+West+Mumbai/Khalapur+Toll+Plaza+Mumbai+Pune+Expressway/Talegaon+Toll+Plaza+Mumbai+Pune+Expressway/Khed+Shivapur+Toll+Plaza+NH48/Anewadi+Toll+Plaza+NH48/Tasawade+Toll+Plaza+NH48/Kini+Toll+Plaza+NH48/Hattargi+Toll+Plaza+NH48/Hirebagewadi+Toll+Plaza+NH48/Mollem+Bridge+NH748/Panaji+Goa/",
            stops = listOf(
                RouteStop("Khalapur Toll Plaza", "Mumbai Pune Expressway"),
                RouteStop("Talegaon Toll Plaza", "Mumbai Pune Expressway"),
                RouteStop("Khed Shivapur Toll Plaza", "NH48"),
                RouteStop("Anewadi Toll Plaza", "NH48"),
                RouteStop("Tasawade Toll Plaza", "NH48"),
                RouteStop("Kini Toll Plaza", "NH48"),
                RouteStop("Hattargi Toll Plaza", "NH48"),
                RouteStop("Hirebagewadi Toll Plaza", "NH48"),
                RouteStop("Mollem Bridge", "NH748")
            ),
            verifyLine = "✅ Road numbers used: Mumbai-Pune Expressway, NH48, NH748. Final 4 km into Panaji via Dayanand Bandodkar Marg.",
            tips = RouteTips(
                bestTime = "05:00 AM start from Mumbai to bypass Vashi and Kalamboli bottleneck.",
                avoid = "Chorla Ghat shortcut at night or during heavy monsoon due to unlit deep potholes and missing crash barriers.",
                localNote = "Belagavi to Goa via Anmod Ghat (NH748) is now wide, paved, and patrolled compared to broken State Highway alternatives.",
                corridorNote = "Borghat section on Mumbai-Pune Expressway has strict 50 km/h camera enforcement on descent; beware heavy cargo trucks occupying right lanes."
            ),
            parking = RouteParking(
                bestLotName = "Patto Multi-Level Car Parking",
                bestLotType = "Public Multi-Level",
                bestLotMapLink = "https://www.google.com/maps/search/Patto+Multi+Level+Car+Parking+Panaji+Goa/",
                backupLotName = "Campal / Dayanand Bandodkar Ground Parking",
                backupLotType = "Public Paved",
                backupLotMapLink = "https://www.google.com/maps/search/Campal+Car+Parking+Panaji+Goa/"
            )
        ),
        SafeRoutePlan(
            id = "delhi-jaipur",
            title = "Delhi to Jaipur (NE4 Expressway Corridor)",
            origin = "Connaught Place, New Delhi",
            destination = "Hawa Mahal, Jaipur",
            distanceApprox = "310 km (Access-Controlled NE4 & NH21)",
            primaryRoads = listOf("NH248A", "NE4 Delhi Mumbai Expressway", "NH21"),
            mapsUrl = "https://www.google.com/maps/dir/Connaught+Place+New+Delhi/Sohna+Toll+Plaza+NH248A/Hilalpur+Toll+Plaza+NE4/Bhandarej+Toll+Plaza+NE4/Dausa+Toll+Plaza+NH21/Hawa+Mahal+Jaipur/",
            stops = listOf(
                RouteStop("Sohna Elevated Highway Toll Plaza", "NH248A"),
                RouteStop("Hilalpur Toll Plaza", "NE4 Delhi Mumbai Expressway"),
                RouteStop("Bhandarej Toll Plaza", "NE4 Delhi Mumbai Expressway"),
                RouteStop("Dausa Toll Plaza", "NH21")
            ),
            verifyLine = "✅ Road numbers used: NH248A, NE4, NH21. Final 3 km through Badi Choupad city approach off NH21.",
            tips = RouteTips(
                bestTime = "06:00 AM departure avoids Gurgaon Cyber City morning rush.",
                avoid = "Old NH48 via Dharuhera-Shahpura during monsoon due to massive waterlogging and flyover construction delays.",
                localNote = "NE4 is an 8-lane access-controlled expressway with 120 km/h legal limit; monitor 16\" low-profile tyre pressure in summer heat.",
                corridorNote = "Rest areas on NE4 are operational every 30 km with clean amenities, fuel stations, and food courts."
            ),
            parking = RouteParking(
                bestLotName = "Jantar Mantar / City Palace Underground Parking",
                bestLotType = "Public Underground",
                bestLotMapLink = "https://www.google.com/maps/search/Jantar+Mantar+Car+Parking+Jaipur/",
                backupLotName = "Ram Niwas Bagh Underground Parking",
                backupLotType = "Public Underground",
                backupLotMapLink = "https://www.google.com/maps/search/Ram+Niwas+Bagh+Parking+Jaipur/"
            )
        ),
        SafeRoutePlan(
            id = "bengaluru-mysuru",
            title = "Bengaluru to Mysuru (NH275 Expressway)",
            origin = "Koramangala, Bengaluru",
            destination = "Mysore Palace, Mysuru",
            distanceApprox = "145 km (Access-Controlled 10-Lane NH275)",
            primaryRoads = listOf("NICE Ring Road", "NH275 Bengaluru Mysuru Expressway"),
            mapsUrl = "https://www.google.com/maps/dir/Koramangala+Bengaluru/NICE+Road+Toll+Plaza+Bengaluru/Kaniminike+Toll+Plaza+NH275/Sheshagirihalli+Toll+Plaza+NH275/Gananguru+Toll+Plaza+NH275/Mysore+Palace+Mysuru/",
            stops = listOf(
                RouteStop("NICE Road Toll Plaza", "NICE Ring Road"),
                RouteStop("Kaniminike Toll Plaza", "NH275 Bengaluru Mysuru Expressway"),
                RouteStop("Sheshagirihalli Toll Plaza", "NH275 Bengaluru Mysuru Expressway"),
                RouteStop("Gananguru Toll Plaza", "NH275 Bengaluru Mysuru Expressway")
            ),
            verifyLine = "✅ Road numbers used: NICE Ring Road, NH275. Final 2.5 km from Columbia Asia Junction into Palace grounds via Bengaluru-Nilgiri Road.",
            tips = RouteTips(
                bestTime = "06:00 AM to 08:00 AM weekdays for uninterrupted cruising.",
                avoid = "Sunday evening return (5:00 PM - 9:30 PM) near Kaniminike toll due to bottleneck merging into Bangalore city.",
                localNote = "2-wheelers and tractors strictly prohibited on main carriageway; keep to 100 km/h limit enforced by sectional speed cameras.",
                corridorNote = "Fully fenced with continuous concrete median; zero pedestrian crossings."
            ),
            parking = RouteParking(
                bestLotName = "Mysore Palace South Gate Tourist Vehicle Parking",
                bestLotType = "Heritage / Dedicated Paved",
                bestLotMapLink = "https://www.google.com/maps/search/Mysore+Palace+Car+Parking+South+Gate/",
                backupLotName = "Dasara Exhibition Grounds Parking",
                backupLotType = "Public Paved",
                backupLotMapLink = "https://www.google.com/maps/search/Dasara+Exhibition+Grounds+Parking+Mysuru/"
            )
        ),
        SafeRoutePlan(
            id = "delhi-lucknow",
            title = "Delhi NCR to Lucknow (Yamuna & Agra-Lucknow Expressways)",
            origin = "Noida, Delhi NCR",
            destination = "Hazratganj, Lucknow",
            distanceApprox = "515 km (Access-Controlled 6-Lane Expressways)",
            primaryRoads = listOf("Yamuna Expressway", "Agra Lucknow Expressway"),
            mapsUrl = "https://www.google.com/maps/dir/Noida+Delhi+NCR/Jewar+Toll+Plaza+Yamuna+Expressway/Khandauli+Toll+Plaza+Yamuna+Expressway/Fatehabad+Toll+Plaza+Agra+Lucknow+Expressway/Kannauj+Toll+Plaza+Agra+Lucknow+Expressway/Hazratganj+Lucknow/",
            stops = listOf(
                RouteStop("Jewar Toll Plaza", "Yamuna Expressway"),
                RouteStop("Khandauli Toll Plaza", "Yamuna Expressway"),
                RouteStop("Fatehabad Toll Plaza", "Agra Lucknow Expressway"),
                RouteStop("Kannauj Toll Plaza", "Agra Lucknow Expressway")
            ),
            verifyLine = "✅ Road numbers used: Yamuna Expressway, Agra-Lucknow Expressway. Final 5 km from Mohan Road interchange via Ring Road into Hazratganj.",
            tips = RouteTips(
                bestTime = "05:30 AM to 06:30 AM departure.",
                avoid = "Winter months (Dec 15 - Jan 31) between 11:00 PM and 08:00 AM due to dense smog/fog zero-visibility conditions.",
                localNote = "Both are 6-lane access-controlled concrete/bituminous expressways with continuous W-beam crash barriers and wire-rope medians.",
                corridorNote = "Yamuna Expressway has concrete surface; check cold tyre pressures before departure to avoid overheating on i20 N Line tyres."
            ),
            parking = RouteParking(
                bestLotName = "Hazratganj Multi-Level Car Parking",
                bestLotType = "Public Multi-Level",
                bestLotMapLink = "https://www.google.com/maps/search/Hazratganj+Multi+Level+Parking+Lucknow/",
                backupLotName = "Janeshwar Mishra Park Car Parking",
                backupLotType = "Public Paved",
                backupLotMapLink = "https://www.google.com/maps/search/Janeshwar+Mishra+Park+Parking+Lucknow/"
            )
        ),
        SafeRoutePlan(
            id = "chennai-tirupati",
            title = "Chennai to Tirupati (Divided ORR & NH716)",
            origin = "Anna Nagar, Chennai",
            destination = "Tirumala Hills, Tirupati",
            distanceApprox = "155 km (Divided Chennai ORR & NH716)",
            primaryRoads = listOf("Chennai ORR", "NH716"),
            mapsUrl = "https://www.google.com/maps/dir/Anna+Nagar+Chennai/Surapet+Toll+Plaza+Chennai+ORR/Pattabiram+Bridge+NH716/Tiruttani+Bypass+NH716/Puttur+Toll+Plaza+NH716/Alipiri+Toll+Gate+Tirupati/",
            stops = listOf(
                RouteStop("Surapet Toll Plaza", "Chennai ORR"),
                RouteStop("Pattabiram Bridge", "NH716"),
                RouteStop("Tiruttani Bypass", "NH716"),
                RouteStop("Puttur Toll Plaza", "NH716")
            ),
            verifyLine = "✅ Road numbers used: Chennai ORR, NH716. Ghat climb to Tirumala is managed single-direction toll road from Alipiri.",
            tips = RouteTips(
                bestTime = "04:30 AM start allows reaching Alipiri security check before 07:30 AM.",
                avoid = "Tiruttani town center market shortcut; always take the outer 4-lane bypass.",
                localNote = "Special Entry Darshan (SED) ₹300 ticket must be pre-booked online with Aadhaar. Ghat road up to Tirumala has a mandatory 28-minute minimum descent/ascent time rule for safety.",
                corridorNote = "FASTag accepted at Alipiri toll. Breathalyzer screening active for all drivers at Alipiri security counter."
            ),
            parking = RouteParking(
                bestLotName = "Medaramitta Car Parking Tirumala",
                bestLotType = "Temple Trust (TTD) Dedicated",
                bestLotMapLink = "https://www.google.com/maps/search/Medaramitta+Car+Parking+Tirumala/",
                backupLotName = "Rambagicha Car Parking Tirumala",
                backupLotType = "Temple Trust (TTD) Multi-bay",
                backupLotMapLink = "https://www.google.com/maps/search/Rambagicha+Parking+Tirumala/"
            )
        ),
        SafeRoutePlan(
            id = "hyderabad-vijayawada",
            title = "Hyderabad to Vijayawada (Divided ORR & NH65)",
            origin = "Gachibowli, Hyderabad",
            destination = "Kanaka Durga Temple, Vijayawada",
            distanceApprox = "290 km (Divided 4/6-lane NH65 Corridor)",
            primaryRoads = listOf("Hyderabad ORR", "NH65"),
            mapsUrl = "https://www.google.com/maps/dir/Gachibowli+Hyderabad/Pedda+Amberpet+Toll+Plaza+Hyderabad+ORR/Pantangi+Toll+Plaza+NH65/Korlapahad+Toll+Plaza+NH65/Chillakallu+Toll+Plaza+NH65/Keesara+Toll+Plaza+NH65/Kanaka+Durga+Temple+Vijayawada/",
            stops = listOf(
                RouteStop("Pedda Amberpet Toll Plaza", "Hyderabad ORR"),
                RouteStop("Pantangi Toll Plaza", "NH65"),
                RouteStop("Korlapahad Toll Plaza", "NH65"),
                RouteStop("Chillakallu Toll Plaza", "NH65"),
                RouteStop("Keesara Toll Plaza", "NH65")
            ),
            verifyLine = "✅ Road numbers used: Hyderabad ORR, NH65. Final 2 km off NH65 via Prakasam Barrage road to temple foot hill parking.",
            tips = RouteTips(
                bestTime = "05:00 AM to beat LB Nagar city traffic exit.",
                avoid = "Suryapet bypass crossing during peak evening commercial transport hours.",
                localNote = "NH65 is fully 4/6-lane divided with wide shoulders and regular petrol pumps with clean restrooms (7 Food Courts corridor).",
                corridorNote = "1033 NHAI ambulance and patrol vans regularly stationed at every toll plaza."
            ),
            parking = RouteParking(
                bestLotName = "Kanaka Durga Temple Ghat Road Multi-level Parking",
                bestLotType = "Temple Multi-Level",
                bestLotMapLink = "https://www.google.com/maps/search/Kanaka+Durga+Temple+Car+Parking+Vijayawada/",
                backupLotName = "Prakasam Barrage Riverside Public Parking",
                backupLotType = "Public Paved",
                backupLotMapLink = "https://www.google.com/maps/search/Prakasam+Barrage+Parking+Vijayawada/"
            )
        ),
        SafeRoutePlan(
            id = "chennai-rameshwaram",
            title = "Chennai to Rameshwaram (Divided NH45 / NH87)",
            origin = "Guindy, Chennai",
            destination = "Ramanathaswamy Temple, Rameshwaram",
            distanceApprox = "560 km (Divided 4-Lane NH45, NH44 & NH87)",
            primaryRoads = listOf("NH45", "NH44", "NH87"),
            mapsUrl = "https://www.google.com/maps/dir/Guindy+Chennai/Paranur+Toll+Plaza+NH45/Athur+Toll+Plaza+NH45/Vikravandi+Toll+Plaza+NH45/Sengurichi+Toll+Plaza+NH45/Samayanallur+Toll+Plaza+NH44/Kappalur+Toll+Plaza+NH44/Paramakudi+Bridge+NH87/Pamban+Bridge+NH87/Ramanathaswamy+Temple+Rameshwaram/",
            stops = listOf(
                RouteStop("Paranur Toll Plaza", "NH45"),
                RouteStop("Athur Toll Plaza", "NH45"),
                RouteStop("Vikravandi Toll Plaza", "NH45"),
                RouteStop("Sengurichi Toll Plaza", "NH45"),
                RouteStop("Samayanallur Toll Plaza", "NH44"),
                RouteStop("Kappalur Toll Plaza", "NH44"),
                RouteStop("Paramakudi Bridge", "NH87"),
                RouteStop("Pamban Bridge", "NH87")
            ),
            verifyLine = "✅ Road numbers used: NH45, NH44, NH87. Final 1.5 km to temple north car street leaves NH87.",
            tips = RouteTips(
                bestTime = "04:30 AM departure from Chennai.",
                avoid = "Crossing Chengalpattu after 07:00 AM on long weekends due to bottleneck toll queues.",
                localNote = "Ramanathaswamy Temple morning 22 Theertham holy baths open 06:00 AM to 12:00 PM; wear traditional dress.",
                corridorNote = "Entire corridor until Pamban is divided 4-lane NH with continuous crash barriers and excellent road surface for low-profile tyres."
            ),
            parking = RouteParking(
                bestLotName = "Temple Trust Tourist Vehicle Parking Rameshwaram",
                bestLotType = "Temple Trust Open Paved",
                bestLotMapLink = "https://www.google.com/maps/search/Rameshwaram+Temple+Car+Parking/",
                backupLotName = "Hotel Daiwik Car Parking",
                backupLotType = "Hotel/Private",
                backupLotMapLink = "https://www.google.com/maps/search/Hotel+Daiwik+Rameshwaram+Parking/"
            )
        )
    )
}
