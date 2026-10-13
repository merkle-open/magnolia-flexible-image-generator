import { token } from './Constants.ts';
import { inject, injectable } from 'tsyringe';

export interface MagnoliaApiEndpointsProvider {
	flexibleImagingBase(): string;
}

export interface Config {
	magnoliaApisProvider: MagnoliaApiEndpointsProvider;
}

export interface FlexibleImageConfigProviderI {
	get(): Config;
}
export const FLEXIBLE_IMAGE_CONFIG_PROVIDER_TOKEN = token('FlexibleImageConfigProviderI');

export interface SerializedConfig {
	flexibleImagingBase: string;
}

@injectable()
export class SerializedFlexibleImageConfigProvider {
	constructor(@inject(FLEXIBLE_IMAGE_CONFIG_PROVIDER_TOKEN) private readonly configProvider: FlexibleImageConfigProviderI) {}

	public get(): SerializedConfig {
		return this.serializeConfig(this.configProvider.get());
	}

	protected serializeConfig(config: Config): SerializedConfig {
		return {
			flexibleImagingBase: config.magnoliaApisProvider.flexibleImagingBase(),
		};
	}

	public deserializeConfig(config: SerializedConfig): Config {
		return {
			magnoliaApisProvider: {
				flexibleImagingBase: () => config.flexibleImagingBase,
			},
		};
	}
}
