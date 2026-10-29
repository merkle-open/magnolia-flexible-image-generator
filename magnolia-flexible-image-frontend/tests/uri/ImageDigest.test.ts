import { testContainer } from '../TestDependencies.ts';
import { describe, expect, test } from 'vitest';
import { ImageDigest } from '../../src/uri/ImageDigest.ts';

const imageDigest = testContainer().resolve<ImageDigest>(ImageDigest);
describe('ImageDigest', () => {
	test('sameInputSameOutput', () => {
		expect(imageDigest.getHash('someInput')).toStrictEqual(imageDigest.getHash('someInput'));
	});

	test('differentInputDifferentOutput', () => {
		expect(imageDigest.getHash('someInput')).not.toStrictEqual(imageDigest.getHash('someOtherInput'));
	});

	test('sameOutputAsBackendImpl', () => {
		expect(imageDigest.getHash('someInput')).toStrictEqual('100766476');
	});
});
