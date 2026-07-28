import { injectable, injectAll } from 'tsyringe';
import { DynamicImageParameter } from '../model/FlexibleParameter.ts';
import { ImageModel } from '../model/ImageModel.ts';
import { ProcessedBundle } from '../model/ProcessedBundle.ts';
import { Asset, Factory, FACTORY_TOKEN } from './ImageModelFactory.ts';

@injectable()
export class ImageModelFactory implements Factory {
	constructor(@injectAll(FACTORY_TOKEN) private readonly factories: Factory[]) {}

	public create(data: Asset, bundle: ProcessedBundle, dynamicImageParameter?: DynamicImageParameter): ImageModel | null {
		return this.factories.map((factory) => factory.create(data, bundle, dynamicImageParameter)).filter((imageModel) => imageModel != null)[0] ?? null;
	}
}
