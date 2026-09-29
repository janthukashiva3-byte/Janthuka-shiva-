package com.example.data

import com.example.data.model.*

object SeedData {
    val STORES = listOf(
        StoreEntity(
            id = "store_apex_tech",
            name = "Apex Tech & Gadgets",
            category = "Electronics & Accessories",
            rating = 4.8f,
            reviewCount = 384,
            distanceKm = 1.2f,
            deliveryTimeMinutes = 15,
            deliveryFee = 1.99,
            minOrder = 10.0,
            address = "402 Market St, Innovation Quarter",
            promoTag = "⚡ 15-min Express Delivery",
            bannerColorHex = "#4F46E5",
            description = "Authorized retailer for premium cables, chargers, audio gear, and device protection."
        ),
        StoreEntity(
            id = "store_evergreen_market",
            name = "Evergreen Home & Pantry",
            category = "Groceries & Essentials",
            rating = 4.7f,
            reviewCount = 1240,
            distanceKm = 0.8f,
            deliveryTimeMinutes = 12,
            deliveryFee = 0.99,
            minOrder = 15.0,
            address = "120 Pinecrest Ave, Downtown",
            promoTag = "🏷️ Free Delivery over $30",
            bannerColorHex = "#059669",
            description = "Daily household staples, eco-cleaning supplies, laundry care, paper goods and home storage."
        ),
        StoreEntity(
            id = "store_aura_wellness",
            name = "Aura Care & Wellness",
            category = "Beauty & Personal Care",
            rating = 4.9f,
            reviewCount = 520,
            distanceKm = 1.6f,
            deliveryTimeMinutes = 18,
            deliveryFee = 1.49,
            minOrder = 12.0,
            address = "78 Lotus Boulevard, Westside",
            promoTag = "✨ Buy 2 Get 15% OFF",
            bannerColorHex = "#DB2777",
            description = "Dermatologist-approved skincare, hydrating serums, mineral sunscreens, and personal hygiene."
        ),
        StoreEntity(
            id = "store_pulse_pharmacy",
            name = "Pulse OTC Health & First Aid",
            category = "OTC Pharmacy & Wellness",
            rating = 4.8f,
            reviewCount = 890,
            distanceKm = 1.1f,
            deliveryTimeMinutes = 14,
            deliveryFee = 1.29,
            minOrder = 8.0,
            address = "215 Civic Center Way, Suite A",
            promoTag = "💊 24/7 OTC Essentials",
            bannerColorHex = "#0284C7",
            description = "Licensed OTC remedies, vitamin supplements, allergy relief, bandages, and wellness kits."
        ),
        StoreEntity(
            id = "store_urban_threads",
            name = "Urban Stitch & Basics",
            category = "Fashion & Apparel",
            rating = 4.6f,
            reviewCount = 210,
            distanceKm = 2.4f,
            deliveryTimeMinutes = 24,
            deliveryFee = 2.49,
            minOrder = 20.0,
            address = "55 S Fashion District Blvd",
            promoTag = "👕 Flat $5 OFF with UTH5",
            bannerColorHex = "#D97706",
            description = "Everyday heavyweight cotton tees, thermal socks, canvas totes, beanies, and loungewear."
        ),
        StoreEntity(
            id = "store_procraft_hardware",
            name = "ProCraft Express Tools",
            category = "Hardware & Daily Tools",
            rating = 4.9f,
            reviewCount = 430,
            distanceKm = 1.9f,
            deliveryTimeMinutes = 20,
            deliveryFee = 2.19,
            minOrder = 15.0,
            address = "88 Industrial Way, Bay 3",
            promoTag = "🔧 Handyman Emergency Kits",
            bannerColorHex = "#EA580C",
            description = "Precision screwdriver kits, Gorilla tape, rechargeable LED work lights, and battery packs."
        ),
        StoreEntity(
            id = "store_paws_haven",
            name = "Paws & Whiskers Depot",
            category = "Pet Supplies",
            rating = 4.8f,
            reviewCount = 612,
            distanceKm = 1.5f,
            deliveryTimeMinutes = 16,
            deliveryFee = 1.79,
            minOrder = 12.0,
            address = "310 Green Meadow Rd",
            promoTag = "🐾 10% OFF on Pet Toys",
            bannerColorHex = "#7C3AED",
            description = "Natural pet shampoos, interactive chew toys, grooming slicker brushes, and pet waste bags."
        ),
        StoreEntity(
            id = "store_page_turner",
            name = "The Paper & Quill Guild",
            category = "Books & Stationery",
            rating = 4.9f,
            reviewCount = 340,
            distanceKm = 1.8f,
            deliveryTimeMinutes = 19,
            deliveryFee = 1.49,
            minOrder = 10.0,
            address = "14 Heritage Lane, Old Town",
            promoTag = "📚 Free Bookmark with Books",
            bannerColorHex = "#475569",
            description = "Dotted journal notebooks, gel pen sets, current bestsellers, planner markers, and sketchpads."
        )
    )

    val PRODUCTS = listOf(
        // Electronics
        ProductEntity(
            id = "prod_usb_c_charger",
            storeId = "store_apex_tech",
            storeName = "Apex Tech & Gadgets",
            name = "65W GaN Dual-Port Fast Charger",
            category = "Electronics & Accessories",
            price = 28.99,
            originalPrice = 39.99,
            rating = 4.9f,
            reviewCount = 142,
            description = "Ultra-compact Gallium Nitride wall charger with USB-C Power Delivery and USB-A Quick Charge. Charges phones and laptops 3x faster.",
            specifications = "Power: 65W Max | Ports: 1x USB-C, 1x USB-A | Weight: 98g | Compatibility: Universal iOS/Android/Mac/PC",
            inStock = true,
            stockCount = 18,
            isBestseller = true,
            isRecommended = true,
            badge = "Bestseller",
            accentColorHex = "#4F46E5"
        ),
        ProductEntity(
            id = "prod_braided_cable",
            storeId = "store_apex_tech",
            storeName = "Apex Tech & Gadgets",
            name = "Braided USB-C to USB-C Cable (2m)",
            category = "Electronics & Accessories",
            price = 11.49,
            originalPrice = 16.99,
            rating = 4.8f,
            reviewCount = 98,
            description = "Military-grade nylon braided 100W PD charging cable with reinforced stress joints and 480Mbps data transfer.",
            specifications = "Length: 2.0 meters | Rating: 100W (20V/5A) | Jacket: Double braided nylon | Bend Lifespan: 25,000+",
            inStock = true,
            stockCount = 42,
            isBestseller = true,
            isRecommended = true,
            badge = "Trending",
            accentColorHex = "#0284C7"
        ),
        ProductEntity(
            id = "prod_anc_earbuds",
            storeId = "store_apex_tech",
            storeName = "Apex Tech & Gadgets",
            name = "AeroPods Pro Wireless Earbuds",
            category = "Electronics & Accessories",
            price = 54.99,
            originalPrice = 79.99,
            rating = 4.7f,
            reviewCount = 86,
            description = "Active noise cancelling wireless earbuds featuring transparency mode, 32-hour battery life with Qi wireless charging case.",
            specifications = "Bluetooth: 5.3 | Water Resistance: IPX5 | Drivers: 11mm Graphene | Battery: 8h buds + 24h case",
            inStock = true,
            stockCount = 12,
            isBestseller = false,
            isRecommended = true,
            badge = "Hot Deal",
            accentColorHex = "#6366F1"
        ),

        // Groceries & Essentials
        ProductEntity(
            id = "prod_eco_detergent",
            storeId = "store_evergreen_market",
            storeName = "Evergreen Home & Pantry",
            name = "Plant-Based Liquid Laundry Detergent (2L)",
            category = "Groceries & Essentials",
            price = 13.99,
            originalPrice = 17.50,
            rating = 4.8f,
            reviewCount = 310,
            description = "Hypoallergenic, ultra-concentrated formula powered by botanical enzymes. Cleans 64 loads without artificial fragrances or dyes.",
            specifications = "Volume: 2.0 Liters | Loads: 64 High-Efficiency Loads | Scent: Crisp Eucalyptus & Lavender | Biodegradable",
            inStock = true,
            stockCount = 35,
            isBestseller = true,
            isRecommended = true,
            badge = "Eco Pick",
            accentColorHex = "#059669"
        ),
        ProductEntity(
            id = "prod_bamboo_towels",
            storeId = "store_evergreen_market",
            storeName = "Evergreen Home & Pantry",
            name = "100% Bamboo Heavy-Duty Paper Towels (6pk)",
            category = "Groceries & Essentials",
            price = 9.99,
            originalPrice = 12.99,
            rating = 4.7f,
            reviewCount = 188,
            description = "FSC certified sustainable bamboo kitchen rolls. Super absorbent 2-ply sheets capable of scrubbing grease without tearing.",
            specifications = "Pack: 6 Rolls | Count: 140 sheets/roll | Material: 100% Organic Bamboo | Bleach-Free",
            inStock = true,
            stockCount = 50,
            isBestseller = false,
            isRecommended = true,
            badge = "Value Pack",
            accentColorHex = "#10B981"
        ),
        ProductEntity(
            id = "prod_organizer_bins",
            storeId = "store_evergreen_market",
            storeName = "Evergreen Home & Pantry",
            name = "Clear Pantry Storage Bins with Handles (Set of 4)",
            category = "Home & Kitchen",
            price = 19.99,
            originalPrice = 27.99,
            rating = 4.9f,
            reviewCount = 74,
            description = "BPA-free shatterproof modular organizers ideal for refrigerator, cabinets, snack closets, and vanity organization.",
            specifications = "Includes: 4 Stackable Bins | Dimensions: 11\" x 7.5\" x 6\" | Material: BPA-Free PET | Dishwasher: Hand wash",
            inStock = true,
            stockCount = 15,
            isBestseller = false,
            isRecommended = false,
            badge = "Home Must-Have",
            accentColorHex = "#0D9488"
        ),

        // Beauty & Skincare
        ProductEntity(
            id = "prod_hyaluronic_serum",
            storeId = "store_aura_wellness",
            storeName = "Aura Care & Wellness",
            name = "Triple-Molecular Hyaluronic Acid Serum (50ml)",
            category = "Beauty & Personal Care",
            price = 22.50,
            originalPrice = 32.00,
            rating = 4.9f,
            reviewCount = 420,
            description = "Deeply penetrating multi-depth hydration serum enriched with Vitamin B5 (Panthenol) to plump fine lines and restore barrier glow.",
            specifications = "Volume: 50 ml / 1.7 fl oz | Skin Type: All, sensitive friendly | Fragrance: None | Paraben-free",
            inStock = true,
            stockCount = 28,
            isBestseller = true,
            isRecommended = true,
            badge = "Top Rated",
            accentColorHex = "#DB2777"
        ),
        ProductEntity(
            id = "prod_mineral_sunscreen",
            storeId = "store_aura_wellness",
            storeName = "Aura Care & Wellness",
            name = "Invisible Shield Daily Mineral Sunscreen SPF 50",
            category = "Beauty & Personal Care",
            price = 18.00,
            originalPrice = 24.00,
            rating = 4.8f,
            reviewCount = 265,
            description = "Non-greasy, zero white-cast zinc oxide formula with niacinamide. Protects against UVA, UVB, and digital blue light.",
            specifications = "SPF: 50+ Broad Spectrum | Weight: 75 ml | Finish: Satin-matte | Reef Safe & Cruelty Free",
            inStock = true,
            stockCount = 30,
            isBestseller = true,
            isRecommended = true,
            badge = "Bestseller",
            accentColorHex = "#E11D48"
        ),

        // OTC Pharmacy & Wellness
        ProductEntity(
            id = "prod_first_aid_kit",
            storeId = "store_pulse_pharmacy",
            storeName = "Pulse OTC Health & First Aid",
            name = "Complete Emergency First Aid Kit (120 Pcs)",
            category = "OTC Pharmacy & Wellness",
            price = 16.49,
            originalPrice = 22.00,
            rating = 4.9f,
            reviewCount = 190,
            description = "Compact waterproof emergency kit stocked with sterile gauze, adhesive bandages, antiseptic wipes, burn gel, shears, and tweezers.",
            specifications = "Piece Count: 120 items | Case: High-denier EVA hard shell | Size: 7.5\" x 5.2\" x 2.4\" | Weight: 350g",
            inStock = true,
            stockCount = 22,
            isBestseller = true,
            isRecommended = true,
            badge = "Essential",
            accentColorHex = "#0284C7"
        ),
        ProductEntity(
            id = "prod_electrolyte_fizzy",
            storeId = "store_pulse_pharmacy",
            storeName = "Pulse OTC Health & First Aid",
            name = "Rapid Hydration Electrolyte Tablets (Pack of 30)",
            category = "OTC Pharmacy & Wellness",
            price = 12.99,
            originalPrice = 16.00,
            rating = 4.7f,
            reviewCount = 145,
            description = "Effervescent recovery tablets with Potassium, Magnesium, Sodium, and Zinc for fast rehydration without excess sugar.",
            specifications = "Count: 3 Tubes (30 tablets total) | Flavor: Lemon Lime & Berry Crisp | Calories: 10 per serving",
            inStock = true,
            stockCount = 40,
            isBestseller = false,
            isRecommended = true,
            badge = "Popular",
            accentColorHex = "#0EA5E9"
        ),

        // Hardware & Tools
        ProductEntity(
            id = "prod_precision_screwdriver",
            storeId = "store_procraft_hardware",
            storeName = "ProCraft Express Tools",
            name = "62-in-1 Precision Magnetic Screwdriver Set",
            category = "Hardware & Daily Tools",
            price = 21.99,
            originalPrice = 29.99,
            rating = 4.9f,
            reviewCount = 210,
            description = "High-grade S2 steel magnetic bits in a push-to-eject aluminum alloy storage case. Perfect for laptops, eyeglasses, gadgets, and watches.",
            specifications = "Bits: 62 S2 Steel Bits | Handle: Ergonomic 360° rotating cap | Case: Aluminum sliding box",
            inStock = true,
            stockCount = 16,
            isBestseller = true,
            isRecommended = true,
            badge = "Pro Choice",
            accentColorHex = "#EA580C"
        ),
        ProductEntity(
            id = "prod_gorilla_tape",
            storeId = "store_procraft_hardware",
            storeName = "ProCraft Express Tools",
            name = "Heavy Duty Waterproof All-Weather Duct Tape",
            category = "Hardware & Daily Tools",
            price = 8.99,
            originalPrice = 11.99,
            rating = 4.8f,
            reviewCount = 156,
            description = "Double-thick adhesive with rugged, weather-resistant shell. Bonds to smooth, rough, and uneven surfaces indoors or outdoors.",
            specifications = "Width: 1.88 inches | Length: 30 Yards | Tensile: 50 lbs/in | Color: Matte Black",
            inStock = true,
            stockCount = 45,
            isBestseller = false,
            isRecommended = false,
            badge = "Heavy Duty",
            accentColorHex = "#C2410C"
        ),

        // Pet Supplies
        ProductEntity(
            id = "prod_interactive_laser_toy",
            storeId = "store_paws_haven",
            storeName = "Paws & Whiskers Depot",
            name = "Automatic Rotating Interactive Cat Laser & Feather Toy",
            category = "Pet Supplies",
            price = 17.99,
            originalPrice = 24.99,
            rating = 4.8f,
            reviewCount = 172,
            description = "Dual-speed motorized laser pointer with random patterns and timer shutoff to keep indoor cats stimulated and active.",
            specifications = "Power: USB-C Rechargeable | Modes: 3 Speed Settings | Auto-Off: 15 minutes | Safe Class II Laser",
            inStock = true,
            stockCount = 19,
            isBestseller = true,
            isRecommended = true,
            badge = "Pet Favorite",
            accentColorHex = "#7C3AED"
        ),

        // Fashion & Apparel
        ProductEntity(
            id = "prod_heavyweight_tee",
            storeId = "store_urban_threads",
            storeName = "Urban Stitch & Basics",
            name = "Heavyweight 240GSM Combed Cotton Oversized Tee",
            category = "Fashion & Apparel",
            price = 18.50,
            originalPrice = 25.00,
            rating = 4.6f,
            reviewCount = 88,
            description = "Premium relaxed-fit crewneck tee made from 100% pre-shrunk carded cotton with drop shoulders and reinforced ribbed collar.",
            specifications = "Material: 100% Cotton 240 GSM | Fit: Boxy relaxed | Sizes: S, M, L, XL | Care: Machine wash cold",
            inStock = true,
            stockCount = 25,
            isBestseller = false,
            isRecommended = true,
            badge = "Trending",
            accentColorHex = "#D97706"
        ),

        // Books & Stationery
        ProductEntity(
            id = "prod_dotted_journal",
            storeId = "store_page_turner",
            storeName = "The Paper & Quill Guild",
            name = "Hardcover 160GSM Dotted Grid Journal (A5)",
            category = "Books & Stationery",
            price = 14.99,
            originalPrice = 19.99,
            rating = 4.9f,
            reviewCount = 120,
            description = "Fountain-pen friendly bleedproof 160GSM bamboo pages, lay-flat thread binding, elastic closure band, dual ribbon markers, and inner back pocket.",
            specifications = "Size: A5 (5.8\" x 8.3\") | Pages: 160 numbered pages | Grid: 5mm Dot Matrix | Cover: Vegan Faux Leather",
            inStock = true,
            stockCount = 32,
            isBestseller = true,
            isRecommended = true,
            badge = "Staff Pick",
            accentColorHex = "#475569"
        ),
        ProductEntity(
            id = "prod_fineliner_pens",
            storeId = "store_page_turner",
            storeName = "The Paper & Quill Guild",
            name = "Fineliner Archival Ink Micro Pens (Set of 8)",
            category = "Books & Stationery",
            price = 12.49,
            originalPrice = 16.00,
            rating = 4.8f,
            reviewCount = 95,
            description = "Precision pigment liners ranging from 0.05mm to 0.8mm and brush tip. Waterproof, fade-proof, and fast-drying.",
            specifications = "Tips: 0.05, 0.1, 0.2, 0.3, 0.5, 0.6, 0.8, Brush | Ink: Black Archival Pigment | Smudge-proof",
            inStock = true,
            stockCount = 20,
            isBestseller = false,
            isRecommended = false,
            badge = "Artist Choice",
            accentColorHex = "#334155"
        )
    )

    val ADDRESSES = listOf(
        AddressEntity(
            id = "addr_home",
            label = "Home",
            street = "742 Evergreen Terrace, Apt 4B",
            city = "Metro City",
            postalCode = "94103",
            isDefault = true
        ),
        AddressEntity(
            id = "addr_work",
            label = "Office",
            street = "500 Howard Street, Floor 12",
            city = "Metro City",
            postalCode = "94105",
            isDefault = false
        ),
        AddressEntity(
            id = "addr_parents",
            label = "Parent's House",
            street = "128 Oakridge Park, West Wing",
            city = "Metro City",
            postalCode = "94118",
            isDefault = false
        )
    )

    val INITIAL_REVIEWS = listOf(
        ReviewEntity(
            targetType = "store",
            targetId = "store_apex_tech",
            author = "Alex Rivera",
            rating = 5.0f,
            comment = "Received my 65W GaN charger in literally 11 minutes! Incredible packaging and completely authentic product.",
            dateText = "Yesterday"
        ),
        ReviewEntity(
            targetType = "store",
            targetId = "store_apex_tech",
            author = "Samantha Lin",
            rating = 4.8f,
            comment = "Super handy for emergency cable replacements when working on deadlines. The rider was very polite.",
            dateText = "3 days ago"
        ),
        ReviewEntity(
            targetType = "product",
            targetId = "prod_usb_c_charger",
            author = "Jordan K.",
            rating = 5.0f,
            comment = "Doesn't overheat even when powering my MacBook and phone at the same time. The GaN tech is top tier.",
            dateText = "2 days ago"
        ),
        ReviewEntity(
            targetType = "product",
            targetId = "prod_first_aid_kit",
            author = "Dr. Elena Rostova",
            rating = 5.0f,
            comment = "Very comprehensive for everyday bumps, burns, and scrapes. Keeping one in my car and one in the workshop.",
            dateText = "Last week"
        )
    )

    val COUPONS = listOf(
        Coupon(
            code = "OMNI50",
            title = "50% OFF Flash Welcome",
            description = "Get 50% discount up to $15 on your order",
            discountPercent = 50,
            maxDiscount = 15.0,
            minOrder = 20.0
        ),
        Coupon(
            code = "QUICKFREE",
            title = "Zero Delivery Fee",
            description = "Free delivery on all orders over $15",
            discountPercent = 100, // special handling for delivery fee
            maxDiscount = 5.0,
            minOrder = 15.0
        ),
        Coupon(
            code = "WEEKEND20",
            title = "20% OFF Super Weekend",
            description = "20% discount on electronics and home supplies",
            discountPercent = 20,
            maxDiscount = 10.0,
            minOrder = 25.0
        ),
        Coupon(
            code = "WELLNESS15",
            title = "15% OFF Pharmacy & Skincare",
            description = "Enjoy 15% off OTC health and wellness essentials",
            discountPercent = 15,
            maxDiscount = 8.0,
            minOrder = 18.0
        )
    )
}

data class Coupon(
    val code: String,
    val title: String,
    val description: String,
    val discountPercent: Int,
    val maxDiscount: Double,
    val minOrder: Double
)
