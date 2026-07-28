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
