# Magnolia flexible image generator
Magnolia image generator that uses json definitions to generate images.

## Setup
1. Create bundlesConfig.json
    - /frontend/config/imaging/bundlesConfig.json:
        ```json
        {
          "scanPaths": ["/frontend/src/patterns", "/frontend/src/views/magnolia"],
          "bundlesDirName": "_bundles"
        }
        ```
2. Configure bundlesConfig.json location 
   - /modules/magnolia-flexible-image-generator/config@bundlesConfigPath=/frontend/config/imaging/bundlesConfig.json
3. Configure bundles in specified location, e.g. /frontend/src/patterns/atom/img/_bundles/16_7-stage.json 
   - ```json
      [
        {
          "bundle": "16_7-stage",
          "description": "ratio 16:7 bundle for stage image, VP S and M: 12cols wide, VP L: 12cols + container padding (breaking out of grid)",
          "ratio": "16:7",
          "imageSizes": [
            {
              "width": 720,
              "media": "720w"
            },
            {
              "width": 1280,
              "media": "1280w"
            },
            {
              "width": 2880,
              "media": "2880w"
            }
          ],
          "customRenditions": [
            {
              "id": "fallbackImage",
              "width": 669
            }
          ]
        }
      ]
      ```
     
## Bundle schema
```json
{
   "$schema": "http://json-schema.org/draft-07/schema#",
   "title": "bundle",
   "type": "array",
   "items": {
      "type": "object",
      "properties": {
         "bundle": {
            "type": "string",
            "description": "bundle name"
         },
         "ratio": {
            "type": "string",
            "description": "e.g. '16:9'. If not specified here or in imageSize, the original image ratio will be preserved"
         },
         "imageSizes": {
            "type": "array",
            "items": {
               "type": "object",
               "properties": {
                  "width": {
                     "type": "integer"
                  },
                  "media": {
                     "type": "string",
                     "description": "The media size for the image (e.g. '2880w')"
                  },
                  "ratio": {
                     "type": "string",
                     "description": "e.g. '16:9'. If not specified here or in the bundle, the original image ratio will be preserved"
                  }
               },
               "required": [ "width", "media" ]
            }
         },
         "customRenditions": {
            "type": "array",
            "items": {
               "type": "object",
               "properties": {
                  "width": {
                     "type": "integer"
                  },
                  "id": {
                     "type": "string",
                     "description": "identifier"
                  },
                  "ratio": {
                     "type": "string",
                     "description": "e.g. '16:9'"
                  }
               },
               "required": [ "width", "id" ]
            }
         }
      },
      "required": [ "bundle", "imageSizes", "customRenditions"]
   }
}
```
## Custom image operations
It is possible to implement custom image operations by extending and binding your own instances:

#### [DynamicImageParameter](src/main/java/com/merkle/oss/magnolia/imaging/flexible/model/DynamicImageParameter.java)
<b>Make sure to create hashCode since it is used for image-caching!</b>
```java
import com.merkle.oss.magnolia.imaging.flexible.model.DynamicImageParameter;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomDynamicImageParameter extends DynamicImageParameter {
   private final boolean triggerCustomOperation;

   public CustomDynamicImageParameter(
           final boolean crop,
           final boolean triggerCustomOperation
   ) {
      super(crop);
      this.triggerCustomOperation = triggerCustomOperation;
   }

   public boolean isTriggerCustomOperation() {
      return triggerCustomOperation;
   }

   @Override
   public Map<String, String> toMap() {
      return Stream.concat(
              super.toMap().entrySet().stream(),
              Map.of(Factory.TRIGGER_CUSTOM_OPERATION_PARAM, isTriggerCustomOperation()).entrySet().stream()
      ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      CustomDynamicImageParameter that = (CustomDynamicImageParameter) o;
      return triggerCustomOperation == that.triggerCustomOperation;
   }

   // used for image-caching path!
   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), triggerCustomOperation);
   }

   @Override
   public String toString() {
      return "CustomDynamicImageParameter{" +
              "triggerCustomOperation=" + triggerCustomOperation +
              "} " + super.toString();
   }

   public static class Factory extends DynamicImageParameter.Factory {
      public static final String TRIGGER_CUSTOM_OPERATION_PARAM = "triggerCustomOperation";

      @Override
      public Optional<CustomDynamicImageParameter> create(final Function<String, Optional<String>> parameterProvider) {
         return super.create(parameterProvider).flatMap(dynamicImageParameter ->
                 parameterProvider.apply(TRIGGER_CUSTOM_OPERATION_PARAM).map(Boolean::parseBoolean).map(triggerCustomOperation ->
                         new CustomDynamicImageParameter(
                                 dynamicImageParameter.isCrop(),
                                 triggerCustomOperation
                         )
                 )
         );
      }
   }
}
```
```xml
<component>
   <type>com.merkle.oss.magnolia.imaging.flexible.model.DynamicImageParameter$Factory</type>
   <implementation>com.somepackage.CustomDynamicImageParameter$Factory</implementation>
</component>
```
#### [ImageOperationProvider](src/main/java/com/merkle/oss/magnolia/imaging/flexible/generator/ImageOperationProvider.java)

```java
import com.merkle.oss.magnolia.imaging.flexible.generator.DefaultImageOperationProvider;
import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.operations.ImageOperationChain;

import java.util.Optional;
import java.util.function.Function;

public class CustomImageOperationProvider extends DefaultImageOperationProvider {

	@Override
	public ImageOperationChain<ParameterProvider<FlexibleParameter>> get(final FlexibleParameter parameter) {
		final ImageOperationChain<ParameterProvider<FlexibleParameter>> chain = super.get(parameter);
		if (get(parameter, CustomDynamicImageParameter::isTriggerCustomOperation).orElse(false)) {
			...
			chain.addOperation(someCustomOperation);
		}
		return chain;
	}

	private <T> Optional<T> get(final FlexibleParameter parameter, final Function<CustomDynamicImageParameter, T> apply) {
		return parameter.getImageParameter()
				.filter(CustomDynamicImageParameter.class::isInstance)
				.map(CustomDynamicImageParameter.class::cast)
				.map(apply);
	}
}
```

```xml

<component>
    <type>com.merkle.oss.magnolia.imaging.flexible.generator.ImageOperationProvider</type>
    <implementation>com.somepackage.CustomImageOperationProvider</implementation>
</component>
```

## Hashed uri configuration
To prevent unnecessary generation of images, hashed uri factory/parser can be bound, which generate a hash over the 
created uri and checks it when parsing, resulting in 404 if it has been modified. 
### Guice binding
```xml
<components>
   ...
   <component>
      <type>com.merkle.oss.magnolia.imaging.flexible.generator.uri.FlexibleImageUriParser</type>
      <implementation>com.merkle.oss.magnolia.imaging.flexible.generator.uri.HashedFlexibleImageUriParser</implementation>
   </component>
   <component>
      <type>com.merkle.oss.magnolia.imaging.flexible.generator.uri.FlexibleImageUriFactory</type>
      <implementation>com.merkle.oss.magnolia.imaging.flexible.generator.uri.HashedFlexibleImageUriFactory</implementation>
   </component>
   ...
</components>
```
### Salt property
```properties
com.merkle.oss.magnolia.imaging.flexible.generator.uri.hash.salt=someEncryptedSalt
```