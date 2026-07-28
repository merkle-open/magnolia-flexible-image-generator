import { inject, injectable } from 'tsyringe';
import { ImageSize, ProcessedBundle } from '../model/ProcessedBundle.ts';
import { CustomRenditions, ImageModel, Rendition } from '../model/ImageModel.ts';
import { DefaultFlexibleParameterToMap, DynamicImageParameter, FlexibleParameter } from '../model/FlexibleParameter.ts';
import { FlexibleImageUriFactory } from '../uri/FlexibleImageUriFactory.ts';
import { Asset, Factory } from './ImageModelFactory.ts';
import { RatioParser } from '../RatioParser.ts';

@injectable()
export class MagnoliaDamImageModelFactory implements Factory {
	private readonly EXCLUDE_FROM_GENERATION_MIME_TYPES = ['image/svg+xml', 'image/gif'];
	private readonly FALLBACK_VERSION = '1';

	constructor(
		@inject(RatioParser) private readonly ratioParser: RatioParser,
		@inject(FlexibleImageUriFactory) private readonly flexibleImageUriFactory: FlexibleImageUriFactory,
	) {}

	public create(data: Asset, bundle: ProcessedBundle, dynamicImageParameter?: DynamicImageParameter): ImageModel | null {
		if (data.id.startsWith('jcr:')) {
			return {
				alt: data.alt,
				assetName: data.name,
				description: data.description,
				original: data.src,
				title: data.title,
				mimeType: data.mimeType,
				srcset: this.getSrcSet(data, bundle, dynamicImageParameter),
				customRenditions: this.getCustomRenditions(data, bundle, dynamicImageParameter),
			};
		}
		return null;
	}

	private getSrcSet(data: Asset, bundle: ProcessedBundle, dynamicImageParameter?: DynamicImageParameter): Rendition[] {
		if (this.shouldNotGenerateImage(data)) {
			return [];
		}
		return bundle.imageSizes.map((size) => this.getRendition(data, size, dynamicImageParameter));
	}
	private getRendition(data: Asset, size: ImageSize, dynamicImageParameter?: DynamicImageParameter): Rendition {
		const ratio = size.ratio ?? data.ratio;
		const parsedRatio = this.ratioParser.parse(ratio);
		return {
			media: size.id,
			src: this.getUrl(data, size.width, ratio, dynamicImageParameter),
			width: size.width,
			height: this.calculateHeight(size.width, parsedRatio),
			ratio: parsedRatio,
		};
	}
	private calculateHeight(width: number, ratio: number): number {
		return width / ratio;
	}

	private getCustomRenditions(data: Asset, bundle: ProcessedBundle, dynamicImageParameter?: DynamicImageParameter): CustomRenditions {
		if (this.shouldNotGenerateImage(data)) {
			return {};
		}
		return bundle.customRenditions.reduce(
			(renditions, size) => ({
				...renditions,
				[size.id]: this.getUrl(data, size.width, size.ratio ?? data.ratio, dynamicImageParameter),
			}),
			{},
		);
	}

	private getUrl(data: Asset, width: number, ratio: string, dynamicImageParameter?: DynamicImageParameter): string {
		const parameter: FlexibleParameter = {
			...data,
			ratio: ratio,
			width: width,
			dynamicImageParameter: dynamicImageParameter,
			version: data.version ?? this.FALLBACK_VERSION,
			toMap: () => DefaultFlexibleParameterToMap(parameter),
		};
		return this.flexibleImageUriFactory.create(parameter);
	}

	private shouldNotGenerateImage(data: Asset): boolean {
		return this.EXCLUDE_FROM_GENERATION_MIME_TYPES.includes(data.mimeType);
	}
}
