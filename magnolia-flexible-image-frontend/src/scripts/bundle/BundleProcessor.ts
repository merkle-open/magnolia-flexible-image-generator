import { Bundle, ImageSize as BundleImageSize } from './Bundle.ts';
import { ImageSize, ProcessedBundle } from '../../model/ProcessedBundle.ts';
import { RatioParser } from '../../RatioParser.ts';

export class BundleProcessor {
	constructor(private readonly ratioParser: RatioParser) {}

	public process(bundle: Bundle): ProcessedBundle {
		return {
			name: bundle.bundle,
			imageSizes: this.processImageSizes(bundle.bundle, bundle.imageSizes, bundle.ratio),
			customRenditions: this.processImageSizes(bundle.bundle, bundle.customRenditions, bundle.ratio),
		};
	}

	private processImageSizes(bundleName: string, imageSizes: BundleImageSize[], bundleRatio?: string): ImageSize[] {
		return imageSizes.map((imageSize) => {
			return {
				ratio: this.validateRatioOrThrow(bundleName, imageSize.ratio ?? bundleRatio),
				width: imageSize.width,
				id: imageSize.id,
			};
		});
	}

	private validateRatioOrThrow(bundleName: string, ratio?: string): string {
		if (ratio != null) {
			try {
				this.ratioParser.parse(ratio);
			} catch (e) {
				throw new Error(`invalid ratio ${ratio} in bundle ${bundleName}`, e);
			}
		}
		return ratio;
	}
}
