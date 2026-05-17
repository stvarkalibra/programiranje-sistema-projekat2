package org.raflab.avanturaservice.messaging;

import lombok.RequiredArgsConstructor;
import org.example.sharedmodule.GuideRatingChangedEventDTO;
import org.raflab.avanturaservice.config.RabbitMQConfig;
import org.raflab.avanturaservice.entityLayer.Adventure;
import org.raflab.avanturaservice.repositoryLayer.AdventureRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuideRatingChangedListener {

    private final AdventureRepository adventureRepository;

    @Transactional
    @RabbitListener(queues = RabbitMQConfig.GUIDE_RATING_QUEUE)
    public void handleGuideRatingChanged(GuideRatingChangedEventDTO event) {
        System.out.println(">>> AVANTURA primila GuideRatingChangedEventDTO: " + event);

        List<Adventure> adventures = adventureRepository.findByGuideId(event.getGuideId());

        if (adventures.isEmpty()) {
            System.out.println(">>> Nema avantura za guideId=" + event.getGuideId());
            return;
        }

        for (Adventure adventure : adventures) {
            double oldPrice = adventure.getBasePrice();
            double newPrice = calculateNewPrice(oldPrice, event.getOldRating(), event.getNewRating());
            adventure.setBasePrice(newPrice);
        }

        adventureRepository.saveAll(adventures);

        System.out.println(">>> Ažurirane cene za " + adventures.size()
                + " avantura vodiča ID=" + event.getGuideId());
    }

    private double calculateNewPrice(double currentPrice, Double oldRating, Double newRating) {
        double oldValue = oldRating == null ? 0.0 : oldRating;
        double newValue = newRating == null ? 0.0 : newRating;

        // Ako vodič postane premium vodič, avanture poskupljuju 10%
        if (oldValue < 4.5 && newValue >= 4.5) {
            return round(currentPrice * 1.10);
        }

        // Ako vodič izgubi premium status, cena se vraća nazad
        if (oldValue >= 4.5 && newValue < 4.5) {
            return round(currentPrice / 1.10);
        }

        // Ako rating padne ispod 3.0, avanture pojeftinjuju 10%
        if (oldValue >= 3.0 && newValue < 3.0) {
            return round(currentPrice * 0.90);
        }

        // Ako se rating vrati iznad 3.0, cena se vraća nazad
        if (oldValue < 3.0 && newValue >= 3.0) {
            return round(currentPrice / 0.90);
        }

        return currentPrice;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}