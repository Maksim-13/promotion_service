package faang.school.promotionservice.entity.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(
        indexName = "#{@environment.getProperty('elasticsearch.indices.cache-promo')}",
        createIndex = false
)
@Setting(
        settingPath = "/elasticsearch/settings/promo-settings.json"
)
public class SearchPromo {

    @Id
    private Long id;

    @MultiField(
            mainField = @Field(type = FieldType.Text, name = "title", analyzer = "russian"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword),
                    @InnerField(suffix = "english", type = FieldType.Text, analyzer = "english")
            }
    )
    private String title;

    @Field(type = FieldType.Text, name = "description", analyzer = "russian")
    private String description;

    @Field(type = FieldType.Text, name = "aboutMe", analyzer = "russian")
    private String aboutMe;

    @MultiField(
            mainField = @Field(type = FieldType.Text, name = "country", analyzer = "standard"),
            otherFields = @InnerField(suffix = "keyword", type = FieldType.Keyword)
    )
    private String country;

    @MultiField(
            mainField = @Field(type = FieldType.Text, name = "city", analyzer = "standard"),
            otherFields = @InnerField(suffix = "keyword", type = FieldType.Keyword)
    )
    private String city;

    @Field(type = FieldType.Keyword, name = "user_id")
    private Long userId;
}
