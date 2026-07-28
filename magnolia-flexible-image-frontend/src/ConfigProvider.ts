import { token } from './Constants.ts';

export interface MagnoliaApiEndpointsProvider {
	flexibleImagingBase(): string;
}

export type Config = {
	magnoliaApisProvider: MagnoliaApiEndpointsProvider;
};

export interface FlexibleImageConfigProviderI {
	get(): Config;
}
export const FLEXIBLE_IMAGE_CONFIG_PROVIDER_TOKEN = token('FlexibleImageConfigProviderI');
