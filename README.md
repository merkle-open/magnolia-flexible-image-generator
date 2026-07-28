# Magnolia flexible image generator
Magnolia image generator that uses json definitions to generate images.

## Submodules
- [Magnolia](magnolia-flexible-image-generator/README.md)

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
