package org.schemata;

import com.google.protobuf.GeneratedMessageV3;
import java.util.List;
import org.schemata.schema.BrandBuilder;
import org.schemata.schema.CampaignBuilder;
import org.schemata.schema.CategoryBuilder;
import org.schemata.schema.ProductBuilder;
import org.schemata.schema.UserBuilder;


public interface SchemaRegistry {

  static List<GeneratedMessageV3> registerSchema() {
    return List.of(UserBuilder.User.newBuilder().build(), UserBuilder.UserEvent.newBuilder().build(),
        UserBuilder.UserActivityEvent.newBuilder().build(), UserBuilder.UserActivityAggregate.newBuilder().build(),
        ProductBuilder.Product.newBuilder().build(), ProductBuilder.ProductEvent.newBuilder().build(),
        CategoryBuilder.Category.newBuilder().build(), CategoryBuilder.CategoryEvent.newBuilder().build(),
        BrandBuilder.Brand.newBuilder().build(), BrandBuilder.BrandEvent.newBuilder().build(),
        CampaignBuilder.Campaign.newBuilder().build(), CategoryBuilder.CategoryEvent.newBuilder().build(),
        CampaignBuilder.CampaignCategoryTrackerEvent.newBuilder().build(),
        CampaignBuilder.CampaignProductTrackerEvent.newBuilder().build());
  }
}
