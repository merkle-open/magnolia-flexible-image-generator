import { testContainer } from '../TestDependencies.ts';
import { describe, expect, test } from 'vitest';
import { FlexibleImageUriFactory } from '../../src/uri/FlexibleImageUriFactory.ts';
import { DefaultDynamicImageParameterToMap, DefaultFlexibleParameterToMap, DynamicImageParameter, FlexibleParameter } from '../../src/model/FlexibleParameter.ts';
import { Asset } from '../../src/factory/ImageModelFactory.ts';

const asset: Asset = {
	id: 'jcr:b3ee7444-4830-4454-abbb-20fc35387032',
	alt: 'image alt text',
	description: 'image description',
	name: 'imageName.jpg',
	src: 'https://magnolia.mock/dam/jcr:b3ee7444-4830-4454-abbb-20fc35387032',
	title: 'image title',
};
const dynamicImageParameter: DynamicImageParameter = {
	crop: true,
	toMap: () => DefaultDynamicImageParameterToMap(dynamicImageParameter),
};
const param: FlexibleParameter = {
	...asset,
	toMap: () => DefaultFlexibleParameterToMap(param),
	version: '1733184000000',
	ratio: '16:9',
	width: 100,
	dynamicImageParameter: dynamicImageParameter,
};
const factory = testContainer().resolve<FlexibleImageUriFactory>(FlexibleImageUriFactory);
describe('FlexibleImageUriFactory', () => {
	test('create', () => {
		expect(factory.create(param)).toStrictEqual(
			'https://magnolia.mock/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/crop/true/ratio/16:9/version/1733184000000/width/100/imageName.jpg',
		);
	});
});
