import 'reflect-metadata';
import { container as rootContainer, DependencyContainer } from 'tsyringe';
import { register as registerFlexibleImageOss } from '../src/Dependencies.ts';
import { RatioParser } from '../src/RatioParser.ts';
import { Config, FLEXIBLE_IMAGE_CONFIG_PROVIDER_TOKEN, FlexibleImageConfigProviderI } from '../src/ConfigProvider.ts';

const container = rootContainer.createChildContainer();

class MockFlexibleImageConfigProviderI implements FlexibleImageConfigProviderI {
	get(): Config {
		return {
			magnoliaApisProvider: {
				flexibleImagingBase: () => 'https://magnolia.mock/author/.imaging/flex',
			},
			saltProvider: () => 'someSalt',
		};
	}
}

function registerConfigs(container: DependencyContainer) {
	container.register(FLEXIBLE_IMAGE_CONFIG_PROVIDER_TOKEN, { useClass: MockFlexibleImageConfigProviderI });
}
export function testContainer() {
	if (!container.isRegistered(RatioParser)) {
		registerConfigs(container); //see chapter config
		registerFlexibleImageOss(container);
	}
	return container;
}
