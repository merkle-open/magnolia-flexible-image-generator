# Magnolia flexible image generator

Magnolia image generator that uses json definitions to generate images.

[//]: # (TODO complete readme)

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
          "ratio": 2.285714285714286,
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
              "name": "fallbackImage",
              "width": 669
            }
          ]
        }
      ]
      ```