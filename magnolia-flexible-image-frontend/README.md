# Magnolia flexible image Frontend

## setup

- Add the following dependencies to your package.json
    - `@merkle-open/magnolia-flexible-image`
    - [`tsyringe`](https://github.com/Microsoft/tsyringe)
    - `reflect-metadata`
- Configure tsconfig.json to include the following settings (`./tsconfig.json`)
    ```json
    {
      "compilerOptions": {
        "experimentalDecorators": true,
        "emitDecoratorMetadata": true
      },
      "include": [
        "node_modules/@merkle-open/magnolia-flexible-image"
      ]
    }
    ```
- Setup Dependencies (`./Dependencies.ts`)
    ```typescript
    import 'reflect-metadata';
    import { container } from 'tsyringe';
    import { register as registerFlexibleImageOss } from '@merkle-open/magnolia-flexible-image';
    import registerConfigs from './config/_Dependencies.ts';

    registerConfigs(container); //see chapter config
    registerFlexibleImageOss(container);

    export { container };
    ```
- Generate bundles during build
    ```json
    "generate-image-bundles": "npx tsx node_modules/@merkle-open/magnolia-flexible-image/src/scripts/generate-bundles.ts app/config/imaging/bundlesConfig.json app/config/imaging/generated-bundles"
    ```

## Config

### [FlexibleImageConfigProviderI](./src/ConfigProvider.ts)

`/app/config/SampleFlexibleImageConfigProvider.ts`

```typescript
import { Config, FlexibleImageConfigProviderI } from '@merkle-open/magnolia-flexible-image';
import { injectable } from 'tsyringe';

@injectable()
export class SampleFlexibleImageConfigProvider implements FlexibleImageConfigProviderI {
    private readonly config: Config = {
        magnoliaApisProvider: {
            flexibleImagingBase: () => `${process.env.MGNL_HOST}/.imaging/flex`,
        },
    };

    public get(): Config {
        return this.config;
    }
}
```

### Binding

`/app/config/_Dependencies.ts`

```typescript
import { DependencyContainer } from 'tsyringe';
import { FLEXIBLE_IMAGE_CONFIG_PROVIDER_TOKEN } from '@merkle-open/magnolia-flexible-image';

export default function register(container: DependencyContainer) {
    container.register(FLEXIBLE_IMAGE_CONFIG_PROVIDER_TOKEN, {useClass: SampleFlexibleImageConfigProvider});
}
```

## Usage

### Model generation

```typescript
import { container } from '../../../Dependencies.ts';
import bundle1x1 from '../../../config/imaging/generated-bundles/1x1.ts';
import { Asset, ImageModelFactory } from '@merkle-open/magnolia-flexible-image';

const imageFactory = container.resolve<ImageModelFactory>(ImageModelFactory);
const asset: Asset = { //should be passed in model from backend
    id: 'jcr:d2cdb6b9-844f-49a2-a177-7f3f501e2361',
    ratio: '1:1',
    title: 'image title',
    description: 'image description',
    alt: 'image alt text',
    name: 'imageName',
    src: 'https://someMagnoliaInstance.com/dam/jcr:d2cdb6b9-844f-49a2-a177-7f3f501e2361/imageName.jpg',
};
const imageModel = imageFactory.create(asset, bundle1x1);
```

## local development

npm link doesn't work with tsyringe, because the app and this lib's tsyringe modules have completely independent (global) variables (see [this comment](https://github.com/microsoft/tsyringe/pull/204#issuecomment-2401656829)).<br>
workaround using symbolic links:

```json
{
  ...
  "scripts": {
      "link-local-flexibleImageOss": "npm link @merkle-open/magnolia-flexible-image && APP_DIR=`pwd` && cd node_modules/@merkle-open/magnolia-flexible-image && npm link ${APP_DIR}/node_modules/tsyringe"
  },
  ...
}
```
