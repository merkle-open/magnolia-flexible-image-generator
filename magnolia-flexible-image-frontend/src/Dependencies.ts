import { DependencyContainer } from 'tsyringe';
import { RatioParser } from './RatioParser.ts';
import { MagnoliaDamImageModelFactory } from './factory/MagnoliaDamImageModelFactory.ts';
import { FACTORY_TOKEN } from './factory/ImageModelFactory.ts';
import { ImageModelFactory } from './factory/CombinedImageModelFactory.ts';
import { FlexibleImageUriFactory } from './uri/FlexibleImageUriFactory.ts';

export function register(container: DependencyContainer) {
	container.register(RatioParser, { useClass: RatioParser });
	container.register(FlexibleImageUriFactory, { useClass: FlexibleImageUriFactory });
	container.register(ImageModelFactory, { useClass: ImageModelFactory });
	container.register(FACTORY_TOKEN, { useClass: MagnoliaDamImageModelFactory });
}
