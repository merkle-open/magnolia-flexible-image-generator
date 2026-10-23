import { DependencyContainer } from 'tsyringe';
import { RatioParser } from './RatioParser.ts';
import { MagnoliaDamImageModelFactory } from './factory/MagnoliaDamImageModelFactory.ts';
import { FACTORY_TOKEN } from './factory/ImageModelFactory.ts';
import { ImageModelFactory } from './factory/CombinedImageModelFactory.ts';
import { FlexibleImageUriFactory } from './uri/FlexibleImageUriFactory.ts';
import {
	Config,
	ConfigDeserializer,
	ConfigSerializer,
	FLEXIBLE_IMAGE_CONFIG_PROVIDER_TOKEN,
	FlexibleImageConfigProviderI,
	SerializedConfig,
	SerializedFlexibleImageConfigProvider,
} from './ConfigProvider.ts';

export function register(container: DependencyContainer) {
	container.register(RatioParser, { useClass: RatioParser });
	container.register(FlexibleImageUriFactory, { useClass: FlexibleImageUriFactory });
	container.register(ImageModelFactory, { useClass: ImageModelFactory });
	container.register(ConfigDeserializer, { useClass: ConfigDeserializer });
	container.register(ConfigSerializer, { useClass: ConfigSerializer });
	container.register(SerializedFlexibleImageConfigProvider, { useClass: SerializedFlexibleImageConfigProvider });
	container.register(FACTORY_TOKEN, { useClass: MagnoliaDamImageModelFactory });
}

export function getContainer(originalContainer: DependencyContainer, serializedFlexibleImageConfig?: SerializedConfig): DependencyContainer {
	if (serializedFlexibleImageConfig) {
		const clientSideFlexibleImageConfigProvider: FlexibleImageConfigProviderI = {
			get(): Config {
				return originalContainer.resolve<ConfigDeserializer>(ConfigDeserializer).deserialize(serializedFlexibleImageConfig);
			},
		};
		return originalContainer.createChildContainer().register(FLEXIBLE_IMAGE_CONFIG_PROVIDER_TOKEN, {
			useValue: clientSideFlexibleImageConfigProvider,
		});
	}
	return originalContainer;
}
