import { token } from './Constants.ts';
import { inject, injectable } from 'tsyringe';

export interface MagnoliaApiEndpointsProvider {
	flexibleImagingBase(): string;
}

export interface Config {
	magnoliaApisProvider: MagnoliaApiEndpointsProvider;
	saltProvider?: () => string; //only required for HashedFlexibleImageUriFactory can't be used on client side
}

export interface FlexibleImageConfigProviderI {
	get(): Config;
}
export const FLEXIBLE_IMAGE_CONFIG_PROVIDER_TOKEN = token('FlexibleImageConfigProviderI');

export interface SerializedConfig {
	//don't include salt - hashed uri must not be used on client side
	flexibleImagingBase: string;
}

@injectable()
export class ConfigSerializer {
	public serialize(config: Config): SerializedConfig {
		return {
			flexibleImagingBase: config.magnoliaApisProvider.flexibleImagingBase(),
		};
	}
}

@injectable()
export class ConfigDeserializer {
	public deserialize(config: SerializedConfig): Config {
		return {
			magnoliaApisProvider: {
				flexibleImagingBase: () => config.flexibleImagingBase,
			},
		};
	}
}

@injectable()
export class SerializedFlexibleImageConfigProvider {
	constructor(
		@inject(FLEXIBLE_IMAGE_CONFIG_PROVIDER_TOKEN) private readonly configProvider: FlexibleImageConfigProviderI,
		@inject(ConfigSerializer) private readonly configSerializer: ConfigSerializer,
	) {}

	public get(): SerializedConfig {
		return this.configSerializer.serialize(this.configProvider.get());
	}
}
