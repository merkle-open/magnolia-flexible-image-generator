import { token } from '../Constants.ts';
import { ProcessedBundle } from '../model/ProcessedBundle.ts';
import { DynamicImageParameter } from '../model/FlexibleParameter.ts';
import { ImageModel } from '../model/ImageModel.ts';

export interface Asset {
	id: string;
	name: string;
	alt: string;
	title: string;
	description: string;
	src: string;
	mimeType?: string;
	version?: string;
	ratio: string;
}

export interface Factory {
	/**
	 * @return an imageModel if the assedId matches (e.g. "jcr:..." for dam) or null
	 */
	create(data: Asset, bundle: ProcessedBundle, dynamicImageParameter?: DynamicImageParameter): ImageModel | null;
}

export const FACTORY_TOKEN = token('ImageModelFactory');
