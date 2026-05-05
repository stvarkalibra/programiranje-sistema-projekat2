package org.raflab.guideservice.facade;

import org.raflab.guideservice.clientLayer.RemoteAdventureDTO;
import java.util.List;

public interface AdventureFacade {
    List<RemoteAdventureDTO> getPremiumReport(Long guideId, Double minPrice);
    Double getRevenue(Long guideId);
    Boolean checkOverload(Long guideId);
    List<RemoteAdventureDTO> getAllInCategory(Long categoryId);
}