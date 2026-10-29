import { describe, expect, test } from 'vitest';
import { BundlesParser } from '../../../src/scripts/bundle/Bundle.ts';
import { resolve } from 'path';

const parser = new BundlesParser();
describe('Bundle Parser', () => {
	test('parse', () => {
		const bundles = parser.parse(resolve(__dirname, './bundlesConfig.json'));
		const sort = (a, b) => a.bundle.localeCompare(b.bundle);
		expect(bundles.sort(sort)).toStrictEqual(
			[
				{
					bundle: '1x1',
					ratio: '1:1',
					imageSizes: [
						{ id: '560w', width: 560 },
						{ id: '1000w', width: 1000 },
						{ id: '2000w', width: 2000 },
					],
					customRenditions: [
						{ id: 'fallbackImage', width: 560 },
						{ id: 'previewImage', width: 10 },
					],
				},
				{
					bundle: '16x9',
					ratio: '16:9',
					imageSizes: [
						{ id: '560w', width: 560 },
						{ id: '1120w', width: 1120 },
						{ id: '2208w', width: 2208 },
					],
					customRenditions: [
						{ id: 'fallbackImage', width: 560 },
						{ id: 'previewImage', width: 16 },
					],
				},
				{
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
				},
				{
					bundle: 'no-custom-renditions',
					imageSizes: [
						{ id: '560w', width: 560, ratio: '1:1' },
						{ id: '1120w', width: 1120 },
						{ id: '2208w', width: 2208 },
					],
				},
				{
					bundle: 'no-image-sizes',
					customRenditions: [
						{ id: 'fallbackImage', width: 560 },
						{ id: 'previewImage', width: 16 },
					],
				},
			].sort(sort),
		);
	});
});
