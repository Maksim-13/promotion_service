package faang.school.promotionservice.repository.nosql;

import faang.school.promotionservice.entity.cache.SearchPromo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElasticPromoRepository extends ElasticsearchRepository<SearchPromo, String> {

}
