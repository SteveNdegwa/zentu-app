package com.zentu.zentu_core.service.enums;

import lombok.Getter;

@Getter
public enum ServiceType {
    // Entertainment & Hosting
    MC("MC"),
    DJ("DJ"),
    LIVE_BAND("Live Band"),
    COMEDIAN("Comedian"),
    DANCER("Dancer"),
    EVENT_HOST("Event Host"),

    // Media & Coverage
    PHOTOGRAPHY("Photography"),
    VIDEOGRAPHY("Videography"),
    DRONE_SHOOT("Drone Shoot"),
    LIVESTREAMING("Livestreaming"),

    // Food & Catering
    CATERING("Catering"),
    CAKE_BAKERY("Cake & Bakery"),
    SNACK_VENDOR("Snack Vendor"),
    DRINK_SUPPLIER("Drink Supplier"),

    // Decor & Setup
    DECORATION("Decoration"),
    TENT_SUPPLIER("Tent Supplier"),
    STAGE_SETUP("Stage Setup"),
    SOUND_SYSTEM("Sound System"),
    LIGHTING("Lighting"),

    // Venues & Rentals
    VENUE("Venue"),
    CONFERENCE_HALL("Conference Hall"),
    MEETING_SPACE("Meeting Space"),

    // Transport & Logistics
    TRANSPORT("Transport"),
    CAR_HIRE("Car Hire"),
    VAN_HIRE("Van Hire"),
    TRUCK_HIRE("Truck Hire"),
    MOTORBIKE_DELIVERY("Motorbike Delivery"),

    // Finance & Community Support
    LOAN_PROVIDER("Loan Provider"),
    SAVINGS_PLANNER("Savings Planner"),
    TREASURY_SUPPORT("Treasury Support"),
    CONTRIBUTION_AGENT("Contribution Agent"),

    // Community Activities
    TEAM_BUILDING("Team Building"),
    FITNESS_TRAINER("Fitness Trainer"),
    MOTIVATIONAL_SPEAKER("Motivational Speaker"),
    COUNSELING("Counseling"),

    // Support Services
    SECURITY("Security"),
    CLEANING("Cleaning"),
    USHERING("Ushering"),

    // Construction & Real Estate
    BUILDING_CONTRACTOR("Building Contractor"),
    ARCHITECTURAL_DESIGN("Architectural Design"),
    INTERIOR_DESIGN("Interior Design"),
    LANDSCAPING("Landscaping"),
    REAL_ESTATE_AGENT("Real Estate Agent"),
    PLUMBING("Plumbing"),
    ELECTRICAL_WORKS("Electrical Works"),
    PAINTING("Painting"),

    // Technology & Digital
    WEB_DESIGN("Web Design"),
    APP_DEVELOPMENT("App Development"),
    IT_SUPPORT("IT Support"),
    DIGITAL_MARKETING("Digital Marketing");

    private final String displayName;

    ServiceType(String displayName) {
        this.displayName = displayName;
    }
}
