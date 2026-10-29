import { describe, expect, test } from 'vitest';
import { BundleProcessor } from '../../../src/scripts/bundle/BundleProcessor.ts';
import { ProcessedBundle } from '../../../src/model/ProcessedBundle.ts';
import { RatioParser } from '../../../src/RatioParser.ts';

const processor = new BundleProcessor(new RatioParser());
describe('Bundle Processor', () => {
	test('process', () => {
		const actual = processor.process({
			bundle: 'stage',
			imageSizes: [
				{ id: '560w', width: 560, ratio: '1:1' },
				{ id: '1120w', width: 1120 },
				{ id: '2208w', width: 2208 },
			],
			customRenditions: [
				{ id: 'fallbackImage', width: 560 },
				{ id: 'previewImage', width: 16 },
			],
		});
		const expected: ProcessedBundle = {
			name: 'stage',
			imageSizes: [
				{ id: '560w', width: 560, ratio: '1:1' },
				{ id: '1120w', width: 1120, ratio: undefined },
				{ id: '2208w', width: 2208, ratio: undefined },
			],
			customRenditions: [
				{ id: 'fallbackImage', width: 560, ratio: undefined },
				{ id: 'previewImage', width: 16, ratio: undefined },
			],
		};
		expect(actual).toStrictEqual(expected);
	});

	test('process no custom renditions', () => {
		const actual = processor.process({
			bundle: 'no-custom-renditions',
			imageSizes: [
				{ id: '560w', width: 560, ratio: '1:1' },
				{ id: '1120w', width: 1120 },
				{ id: '2208w', width: 2208 },
			],
		});
		const expected: ProcessedBundle = {
			name: 'no-custom-renditions',
			imageSizes: [
				{ id: '560w', width: 560, ratio: '1:1' },
				{ id: '1120w', width: 1120, ratio: undefined },
				{ id: '2208w', width: 2208, ratio: undefined },
			],
			customRenditions: [],
		};
		expect(actual).toStrictEqual(expected);
	});

	test('process no image sizes', () => {
		const actual = processor.process({
			bundle: 'no-image-sizes',
			customRenditions: [
				{ id: 'fallbackImage', width: 560 },
				{ id: 'previewImage', width: 16 },
			],
		});
		const expected: ProcessedBundle = {
			name: 'no-image-sizes',
			imageSizes: [],
			customRenditions: [
				{ id: 'fallbackImage', width: 560, ratio: undefined },
				{ id: 'previewImage', width: 16, ratio: undefined },
			],
		};
		expect(actual).toStrictEqual(expected);
	});
});
