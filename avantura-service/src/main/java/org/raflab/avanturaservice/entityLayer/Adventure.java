package org.raflab.avanturaservice.entityLayer;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Adventure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String difficultyLevel;

    private double basePrice;

    private Long guideId;

    @ManyToOne
    private AdventureCategory adventureCategory;

    @OneToMany(mappedBy = "adventure")
    private List<TimeSlot> timeSlotList;

    @OneToMany(mappedBy = "adventure")
    private List<GearRequirement> gearRequirementList;

    @OneToMany(mappedBy = "adventure")
    private List<AdventureReview> adventureReviewList;

    // 🔥 PRIVATNI KONSTRUKTOR ZA BUILDER
    private Adventure(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.difficultyLevel = builder.difficultyLevel;
        this.basePrice = builder.basePrice;
        this.guideId = builder.guideId;
        this.adventureCategory = builder.adventureCategory;
        this.timeSlotList = builder.timeSlotList;
        this.gearRequirementList = builder.gearRequirementList;
        this.adventureReviewList = builder.adventureReviewList;
    }

    // 🔥 ENTRY POINT ZA BUILDER
    public static Builder builder() {
        return new Builder();
    }

    // 🔥 BUILDER KLASA
    public static class Builder {

        private Long id;
        private String title;
        private String description;
        private String difficultyLevel;
        private double basePrice;
        private Long guideId;
        private AdventureCategory adventureCategory;
        private List<TimeSlot> timeSlotList;
        private List<GearRequirement> gearRequirementList;
        private List<AdventureReview> adventureReviewList;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder difficultyLevel(String difficultyLevel) {
            this.difficultyLevel = difficultyLevel;
            return this;
        }

        public Builder basePrice(double basePrice) {
            this.basePrice = basePrice;
            return this;
        }

        public Builder guideId(Long guideId) {
            this.guideId = guideId;
            return this;
        }

        public Builder adventureCategory(AdventureCategory adventureCategory) {
            this.adventureCategory = adventureCategory;
            return this;
        }

        public Builder timeSlotList(List<TimeSlot> timeSlotList) {
            this.timeSlotList = timeSlotList;
            return this;
        }

        public Builder gearRequirementList(List<GearRequirement> gearRequirementList) {
            this.gearRequirementList = gearRequirementList;
            return this;
        }

        public Builder adventureReviewList(List<AdventureReview> adventureReviewList) {
            this.adventureReviewList = adventureReviewList;
            return this;
        }

        // 🔥 FINALNA KONSTRUKCIJA OBJEKTA
        public Adventure build() {
            return new Adventure(this);
        }
    }
}