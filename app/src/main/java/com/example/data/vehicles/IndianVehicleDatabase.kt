package com.example.data.vehicles

data class IndianVehicleSpec(
    val brand: String,
    val makeModel: String,
    val variant: String,
    val category: String, // Hatchback, Sedan, SUV, MPV, EV
    val tyreSpec: String,
    val recommendedPsi: String,
    val fuelType: String,
    val hasTelematics: Boolean,
    val telematicsName: String,
    val oemRsaPhone: String,
    val sosHardwareInstruction: String
)

object IndianVehicleDatabase {
    val vehicles = listOf(
        // Tata Motors (India's leading safety-first automaker)
        IndianVehicleSpec(
            brand = "Tata",
            makeModel = "Tata Nexon",
            variant = "Fearless+ S 1.2 Turbo / EV",
            category = "SUV",
            tyreSpec = "215/60 R16 Aero Alloys",
            recommendedPsi = "32-34 PSI Cold",
            fuelType = "Turbo Petrol / Diesel / EV",
            hasTelematics = true,
            telematicsName = "Tata iRA Connected Car",
            oemRsaPhone = "1800 209 8282",
            sosHardwareInstruction = "SOS emergency call: overhead roof console button • 24x7 Roadside Assistance: 1800 209 8282"
        ),
        IndianVehicleSpec(
            brand = "Tata",
            makeModel = "Tata Punch",
            variant = "Creative 1.2 Revotron / EV",
            category = "SUV",
            tyreSpec = "195/60 R16 Diamond Cut",
            recommendedPsi = "32 PSI Cold",
            fuelType = "Petrol / CNG / EV",
            hasTelematics = true,
            telematicsName = "Tata iRA",
            oemRsaPhone = "1800 209 8282",
            sosHardwareInstruction = "Overhead roof console SOS button • 24x7 Tata RSA: 1800 209 8282"
        ),
        IndianVehicleSpec(
            brand = "Tata",
            makeModel = "Tata Curvv",
            variant = "Accomplished+ 1.2 Hyperion TGDi",
            category = "SUV",
            tyreSpec = "215/55 R18 Aero Alloys",
            recommendedPsi = "35 PSI Cold",
            fuelType = "Turbo Petrol / Diesel / EV",
            hasTelematics = true,
            telematicsName = "Tata iRA Connected Suite",
            oemRsaPhone = "1800 209 8282",
            sosHardwareInstruction = "Roof console SOS assist button • Tata Highway RSA: 1800 209 8282"
        ),
        IndianVehicleSpec(
            brand = "Tata",
            makeModel = "Tata Harrier",
            variant = "Fearless+ 2.0 Kryotec Diesel",
            category = "SUV",
            tyreSpec = "235/60 R18 High-Profile",
            recommendedPsi = "36 PSI Cold",
            fuelType = "Turbo Diesel",
            hasTelematics = true,
            telematicsName = "Tata iRA",
            oemRsaPhone = "1800 209 8282",
            sosHardwareInstruction = "Overhead SOS emergency button with automated crash notification • RSA: 1800 209 8282"
        ),
        IndianVehicleSpec(
            brand = "Tata",
            makeModel = "Tata Safari",
            variant = "Accomplished+ 2.0 Kryotec Diesel",
            category = "SUV",
            tyreSpec = "245/55 R19 Dual-Tone Alloys",
            recommendedPsi = "36 PSI Cold",
            fuelType = "Turbo Diesel",
            hasTelematics = true,
            telematicsName = "Tata iRA",
            oemRsaPhone = "1800 209 8282",
            sosHardwareInstruction = "Roof console emergency SOS switch • Tata 24x7 RSA: 1800 209 8282"
        ),
        IndianVehicleSpec(
            brand = "Tata",
            makeModel = "Tata Altroz",
            variant = "XZ+ 1.2 i-Turbo / DCA",
            category = "Hatchback",
            tyreSpec = "195/55 R16 Laser Alloys",
            recommendedPsi = "32 PSI Cold",
            fuelType = "Petrol / Diesel / CNG",
            hasTelematics = true,
            telematicsName = "Tata iRA",
            oemRsaPhone = "1800 209 8282",
            sosHardwareInstruction = "Call Tata 24x7 Roadside Assistance: 1800 209 8282 or NHAI 1033"
        ),

        // Mahindra
        IndianVehicleSpec(
            brand = "Mahindra",
            makeModel = "Mahindra XUV700",
            variant = "AX7L 2.0 mStallion / 2.2 mHawk",
            category = "SUV",
            tyreSpec = "235/60 R18 Diamond Cut",
            recommendedPsi = "36 PSI Cold",
            fuelType = "Turbo Petrol / Turbo Diesel",
            hasTelematics = true,
            telematicsName = "Mahindra AdrenoX Connect",
            oemRsaPhone = "1800 209 6006",
            sosHardwareInstruction = "Overhead console: dedicated SOS button triggers emergency voice call & GPS coords • RSA: 1800 209 6006"
        ),
        IndianVehicleSpec(
            brand = "Mahindra",
            makeModel = "Mahindra Scorpio-N",
            variant = "Z8L 4x4 2.2 mHawk Diesel",
            category = "SUV",
            tyreSpec = "255/60 R18 All-Terrain",
            recommendedPsi = "34-36 PSI Cold",
            fuelType = "Turbo Diesel",
            hasTelematics = true,
            telematicsName = "Mahindra AdrenoX",
            oemRsaPhone = "1800 209 6006",
            sosHardwareInstruction = "AdrenoX SOS button above rear-view mirror • 24x7 Mahindra RSA: 1800 209 6006"
        ),
        IndianVehicleSpec(
            brand = "Mahindra",
            makeModel = "Mahindra Thar / Roxx",
            variant = "AX7L 4x4 2.0 Turbo / 2.2 Diesel",
            category = "SUV",
            tyreSpec = "255/65 R18 Deep-Tread AT",
            recommendedPsi = "32-34 PSI Cold",
            fuelType = "Turbo Petrol / Turbo Diesel",
            hasTelematics = true,
            telematicsName = "Mahindra AdrenoX",
            oemRsaPhone = "1800 209 6006",
            sosHardwareInstruction = "Roof-mounted SOS button for crash emergency • Mahindra 24x7 Helpline: 1800 209 6006"
        ),
        IndianVehicleSpec(
            brand = "Mahindra",
            makeModel = "Mahindra XUV 3XO",
            variant = "AX7L 1.2 mStallion TGDi",
            category = "SUV",
            tyreSpec = "215/55 R17 Alloys",
            recommendedPsi = "34 PSI Cold",
            fuelType = "Turbo Petrol / Diesel",
            hasTelematics = true,
            telematicsName = "Mahindra AdrenoX",
            oemRsaPhone = "1800 209 6006",
            sosHardwareInstruction = "Overhead SOS button on front dome light console • Mahindra RSA: 1800 209 6006"
        ),

        // Hyundai
        IndianVehicleSpec(
            brand = "Hyundai",
            makeModel = "Hyundai Creta",
            variant = "SX(O) 1.5 Petrol / Diesel",
            category = "SUV",
            tyreSpec = "215/60 R17 Diamond Cut",
            recommendedPsi = "33-35 PSI Cold",
            fuelType = "Petrol / CRDi Diesel",
            hasTelematics = true,
            telematicsName = "Hyundai Bluelink",
            oemRsaPhone = "1800 102 4645",
            sosHardwareInstruction = "Inside rear-view mirror (IRVM): RSA button (Middle for breakdown) • SOS button (Left for accident/medical)"
        ),
        IndianVehicleSpec(
            brand = "Hyundai",
            makeModel = "Hyundai Venue",
            variant = "SX(O) 1.0 Turbo / 1.5 Diesel",
            category = "SUV",
            tyreSpec = "215/60 R16 Diamond Cut",
            recommendedPsi = "33 PSI Cold",
            fuelType = "1.0 Turbo Petrol / Diesel",
            hasTelematics = true,
            telematicsName = "Hyundai Bluelink",
            oemRsaPhone = "1800 102 4645",
            sosHardwareInstruction = "IRVM mirror console: RSA (Middle), SOS (Left) • 24x7 Hyundai RSA: 1800 102 4645"
        ),
        IndianVehicleSpec(
            brand = "Hyundai",
            makeModel = "Hyundai i20",
            variant = "Asta(O) 1.2 Petrol / Turbo",
            category = "Hatchback",
            tyreSpec = "195/55 R16 Alloys",
            recommendedPsi = "33 PSI Cold",
            fuelType = "Petrol",
            hasTelematics = true,
            telematicsName = "Hyundai Bluelink",
            oemRsaPhone = "1800 102 4645",
            sosHardwareInstruction = "IRVM buttons: Left SOS button connects to emergency operator • Hyundai RSA: 1800 102 4645"
        ),
        IndianVehicleSpec(
            brand = "Hyundai",
            makeModel = "Hyundai Verna",
            variant = "SX(O) 1.5 Turbo GDi",
            category = "Sedan",
            tyreSpec = "205/55 R16 Alloys",
            recommendedPsi = "33-35 PSI Cold",
            fuelType = "Turbo Petrol (95 Octane)",
            hasTelematics = true,
            telematicsName = "Hyundai Bluelink",
            oemRsaPhone = "1800 102 4645",
            sosHardwareInstruction = "IRVM mirror buttons: SOS (Left), RSA (Middle) • Hyundai 24x7 RSA: 1800 102 4645"
        ),
        IndianVehicleSpec(
            brand = "Hyundai",
            makeModel = "Hyundai Exter",
            variant = "SX(O) Connect 1.2 Kappa",
            category = "SUV",
            tyreSpec = "175/65 R15 Dual-Tone",
            recommendedPsi = "32 PSI Cold",
            fuelType = "Petrol / CNG",
            hasTelematics = true,
            telematicsName = "Hyundai Bluelink",
            oemRsaPhone = "1800 102 4645",
            sosHardwareInstruction = "IRVM emergency SOS & RSA buttons • Hyundai Helpline: 1800 102 4645"
        ),

        // Maruti Suzuki
        IndianVehicleSpec(
            brand = "Maruti Suzuki",
            makeModel = "Maruti Suzuki Brezza",
            variant = "ZXi+ 1.5 Smart Hybrid",
            category = "SUV",
            tyreSpec = "215/60 R16 Dual-Tone Alloys",
            recommendedPsi = "32-34 PSI Cold",
            fuelType = "1.5 Petrol Smart Hybrid",
            hasTelematics = true,
            telematicsName = "Suzuki Connect",
            oemRsaPhone = "1800 102 1800",
            sosHardwareInstruction = "Overhead roof console: red SOS emergency assist button • Maruti On-Road Service (MOS): 1800 102 1800"
        ),
        IndianVehicleSpec(
            brand = "Maruti Suzuki",
            makeModel = "Maruti Suzuki Grand Vitara",
            variant = "Alpha+ Strong Hybrid / AllGrip",
            category = "SUV",
            tyreSpec = "215/60 R17 Precision Cut",
            recommendedPsi = "33 PSI Cold",
            fuelType = "Hybrid / Petrol AWD",
            hasTelematics = true,
            telematicsName = "Suzuki Connect",
            oemRsaPhone = "1800 102 1800",
            sosHardwareInstruction = "Roof-mounted SOS button with automated crash notification • 24x7 Maruti RSA: 1800 102 1800"
        ),
        IndianVehicleSpec(
            brand = "Maruti Suzuki",
            makeModel = "Maruti Suzuki Baleno",
            variant = "Alpha 1.2 DualJet",
            category = "Hatchback",
            tyreSpec = "195/55 R16 Precision Alloys",
            recommendedPsi = "32 PSI Cold",
            fuelType = "Petrol / CNG",
            hasTelematics = true,
            telematicsName = "Suzuki Connect",
            oemRsaPhone = "1800 102 1800",
            sosHardwareInstruction = "Suzuki Connect app SOS trigger or call Maruti RSA: 1800 102 1800"
        ),
        IndianVehicleSpec(
            brand = "Maruti Suzuki",
            makeModel = "Maruti Suzuki Fronx",
            variant = "Alpha 1.0 Turbo BoosterJet",
            category = "SUV",
            tyreSpec = "195/60 R16 Alloys",
            recommendedPsi = "32 PSI Cold",
            fuelType = "Turbo Petrol / DualJet",
            hasTelematics = true,
            telematicsName = "Suzuki Connect",
            oemRsaPhone = "1800 102 1800",
            sosHardwareInstruction = "Suzuki Connect emergency assist button on roof console • 24x7 RSA: 1800 102 1800"
        ),
        IndianVehicleSpec(
            brand = "Maruti Suzuki",
            makeModel = "Maruti Suzuki Swift",
            variant = "ZXi+ 1.2 Z-Series Dual VVT",
            category = "Hatchback",
            tyreSpec = "185/65 R15 Alloys",
            recommendedPsi = "32 PSI Cold",
            fuelType = "Petrol",
            hasTelematics = true,
            telematicsName = "Suzuki Connect",
            oemRsaPhone = "1800 102 1800",
            sosHardwareInstruction = "Call Maruti 24x7 Roadside Assistance: 1800 102 1800 or NHAI 1033"
        ),
        IndianVehicleSpec(
            brand = "Maruti Suzuki",
            makeModel = "Maruti Suzuki Ertiga",
            variant = "ZXi+ 1.5 Smart Hybrid",
            category = "MPV",
            tyreSpec = "185/65 R15 Alloys",
            recommendedPsi = "34-36 PSI Cold",
            fuelType = "Petrol / CNG",
            hasTelematics = true,
            telematicsName = "Suzuki Connect",
            oemRsaPhone = "1800 102 1800",
            sosHardwareInstruction = "Suzuki Connect SOS & 24x7 Roadside Assistance: 1800 102 1800"
        ),

        // Kia
        IndianVehicleSpec(
            brand = "Kia",
            makeModel = "Kia Seltos",
            variant = "GTX+ 1.5 Turbo GDi / Diesel",
            category = "SUV",
            tyreSpec = "215/55 R18 Crystal Cut",
            recommendedPsi = "35 PSI Cold",
            fuelType = "Turbo Petrol / Diesel",
            hasTelematics = true,
            telematicsName = "Kia Connect",
            oemRsaPhone = "1800 108 5000",
            sosHardwareInstruction = "IRVM buttons: SOS (Left), RSA (Middle), Call Centre (Right) • 24x7 Kia RSA: 1800 108 5000"
        ),
        IndianVehicleSpec(
            brand = "Kia",
            makeModel = "Kia Sonet",
            variant = "GTX+ 1.0 Turbo / 1.5 Diesel",
            category = "SUV",
            tyreSpec = "215/60 R16 Alloys",
            recommendedPsi = "33 PSI Cold",
            fuelType = "Turbo Petrol / Diesel",
            hasTelematics = true,
            telematicsName = "Kia Connect",
            oemRsaPhone = "1800 108 5000",
            sosHardwareInstruction = "IRVM SOS button for accident assistance • Kia 24x7 RSA: 1800 108 5000"
        ),
        IndianVehicleSpec(
            brand = "Kia",
            makeModel = "Kia Carens",
            variant = "Luxury Plus 1.5 Turbo / Diesel",
            category = "MPV",
            tyreSpec = "205/65 R16 Dual-Tone",
            recommendedPsi = "35 PSI Cold",
            fuelType = "Turbo Petrol / Diesel",
            hasTelematics = true,
            telematicsName = "Kia Connect",
            oemRsaPhone = "1800 108 5000",
            sosHardwareInstruction = "IRVM Kia Connect buttons: SOS (Left), RSA (Middle) • Kia Helpline: 1800 108 5000"
        ),

        // Toyota
        IndianVehicleSpec(
            brand = "Toyota",
            makeModel = "Toyota Innova Hycross",
            variant = "ZX(O) 2.0 Strong Hybrid",
            category = "MPV",
            tyreSpec = "225/50 R18 Super Chrome",
            recommendedPsi = "35 PSI Cold",
            fuelType = "Petrol Strong Hybrid",
            hasTelematics = true,
            telematicsName = "Toyota i-Connect",
            oemRsaPhone = "1800 425 0001",
            sosHardwareInstruction = "Overhead console e-Call / SOS button • Toyota 24x7 Roadside Assistance: 1800 425 0001"
        ),
        IndianVehicleSpec(
            brand = "Toyota",
            makeModel = "Toyota Fortuner",
            variant = "4x4 2.8 Diesel AT",
            category = "SUV",
            tyreSpec = "265/60 R18 Rugged Alloys",
            recommendedPsi = "32-34 PSI Cold",
            fuelType = "Heavy Turbo Diesel",
            hasTelematics = true,
            telematicsName = "Toyota i-Connect",
            oemRsaPhone = "1800 425 0001",
            sosHardwareInstruction = "Toyota 24x7 Roadside Assistance: 1800 425 0001 • NHAI Patrol: 1033"
        ),
        IndianVehicleSpec(
            brand = "Toyota",
            makeModel = "Toyota Urban Cruiser Hyryder",
            variant = "V 1.5 Strong Hybrid / AWD",
            category = "SUV",
            tyreSpec = "215/60 R17 Alloys",
            recommendedPsi = "33 PSI Cold",
            fuelType = "Strong Hybrid / Petrol",
            hasTelematics = true,
            telematicsName = "Toyota i-Connect",
            oemRsaPhone = "1800 425 0001",
            sosHardwareInstruction = "Roof console SOS assist switch • 24x7 Toyota RSA: 1800 425 0001"
        ),

        // Honda
        IndianVehicleSpec(
            brand = "Honda",
            makeModel = "Honda City",
            variant = "ZX 1.5 i-VTEC / e:HEV Hybrid",
            category = "Sedan",
            tyreSpec = "185/55 R16 Diamond Cut",
            recommendedPsi = "32-33 PSI Cold",
            fuelType = "Petrol / Strong Hybrid",
            hasTelematics = true,
            telematicsName = "Honda Connect",
            oemRsaPhone = "1800 113 121",
            sosHardwareInstruction = "Honda Connect app emergency alert • Honda 24x7 Roadside Assistance: 1800 113 121"
        ),
        IndianVehicleSpec(
            brand = "Honda",
            makeModel = "Honda Elevate",
            variant = "ZX 1.5 i-VTEC CVT",
            category = "SUV",
            tyreSpec = "215/55 R17 Dual Tone",
            recommendedPsi = "33 PSI Cold",
            fuelType = "Naturally Aspirated Petrol",
            hasTelematics = true,
            telematicsName = "Honda Connect",
            oemRsaPhone = "1800 113 121",
            sosHardwareInstruction = "Honda Connect emergency service & RSA: 1800 113 121 • NHAI 1033"
        ),

        // Volkswagen & Skoda
        IndianVehicleSpec(
            brand = "Volkswagen",
            makeModel = "Volkswagen Virtus",
            variant = "Topline 1.0 TSI / GT Plus 1.5 DSG",
            category = "Sedan",
            tyreSpec = "205/55 R16 Razor Alloys",
            recommendedPsi = "33 PSI Cold",
            fuelType = "Turbo Petrol (95 Octane recommended)",
            hasTelematics = true,
            telematicsName = "My Volkswagen Connect Plus",
            oemRsaPhone = "1800 102 0909",
            sosHardwareInstruction = "Overhead console: dedicated SOS button • 24x7 Volkswagen Roadside Assistance: 1800 102 0909"
        ),
        IndianVehicleSpec(
            brand = "Volkswagen",
            makeModel = "Volkswagen Taigun",
            variant = "Topline 1.0 TSI / GT Plus 1.5 DSG",
            category = "SUV",
            tyreSpec = "205/55 R17 Manila Alloys",
            recommendedPsi = "34 PSI Cold",
            fuelType = "Turbo Petrol (95 Octane recommended)",
            hasTelematics = true,
            telematicsName = "My Volkswagen Connect Plus",
            oemRsaPhone = "1800 102 0909",
            sosHardwareInstruction = "Overhead SOS switch • 24x7 Volkswagen RSA: 1800 102 0909"
        ),
        IndianVehicleSpec(
            brand = "Skoda",
            makeModel = "Skoda Slavia",
            variant = "Style 1.0 TSI / Monte Carlo 1.5 DSG",
            category = "Sedan",
            tyreSpec = "205/55 R16 Black Alloys",
            recommendedPsi = "33 PSI Cold",
            fuelType = "Turbo Petrol (95 Octane recommended)",
            hasTelematics = true,
            telematicsName = "MySkoda Connect",
            oemRsaPhone = "1800 209 4655",
            sosHardwareInstruction = "Roof console emergency call button • 24x7 Skoda Roadside Assistance: 1800 209 4655"
        ),
        IndianVehicleSpec(
            brand = "Skoda",
            makeModel = "Skoda Kushaq",
            variant = "Style 1.0 TSI / Monte Carlo 1.5 DSG",
            category = "SUV",
            tyreSpec = "205/55 R17 Vega Alloys",
            recommendedPsi = "34 PSI Cold",
            fuelType = "Turbo Petrol (95 Octane recommended)",
            hasTelematics = true,
            telematicsName = "MySkoda Connect",
            oemRsaPhone = "1800 209 4655",
            sosHardwareInstruction = "Roof console emergency button • 24x7 Skoda RSA: 1800 209 4655"
        ),

        // MG Motors
        IndianVehicleSpec(
            brand = "MG",
            makeModel = "MG Hector",
            variant = "Savvy Pro 1.5 Turbo / 2.0 Diesel",
            category = "SUV",
            tyreSpec = "215/55 R18 Dual Tone",
            recommendedPsi = "35 PSI Cold",
            fuelType = "Turbo Petrol / Diesel",
            hasTelematics = true,
            telematicsName = "MG i-SMART",
            oemRsaPhone = "1800 100 6464",
            sosHardwareInstruction = "Overhead console e-Call / SOS button • MG Pulse Hub 24x7: 1800 100 6464"
        ),
        IndianVehicleSpec(
            brand = "MG",
            makeModel = "MG ZS EV",
            variant = "Exclusive Pro 50.3 kWh",
            category = "EV",
            tyreSpec = "215/55 R17 Aero Alloys",
            recommendedPsi = "36 PSI Cold",
            fuelType = "Electric (50.3 kWh Battery)",
            hasTelematics = true,
            telematicsName = "MG i-SMART EV",
            oemRsaPhone = "1800 100 6464",
            sosHardwareInstruction = "Overhead SOS button with automatic battery isolator trigger • 24x7 MG RSA: 1800 100 6464"
        )
    )

    fun search(query: String, brandFilter: String = "All"): List<IndianVehicleSpec> {
        return vehicles.filter { spec ->
            val matchesBrand = brandFilter == "All" || spec.brand.equals(brandFilter, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    spec.makeModel.contains(query, ignoreCase = true) ||
                    spec.variant.contains(query, ignoreCase = true) ||
                    spec.brand.contains(query, ignoreCase = true) ||
                    spec.category.contains(query, ignoreCase = true)
            matchesBrand && matchesQuery
        }
    }
}
