import { testContainer } from '../TestDependencies.ts';
import { describe, expect, test } from 'vitest';
import { FlexibleImageUriFactory } from '../../src/uri/FlexibleImageUriFactory.ts';
import { DefaultDynamicImageParameterToMap, DefaultFlexibleParameterToMap, DynamicImageParameter, FlexibleParameter } from '../../src/model/FlexibleParameter.ts';
import { Asset } from '../../src/factory/ImageModelFactory.ts';
import { HashedFlexibleImageUriFactory } from '../../src/uri/HashedFlexibleImageUriFactory.ts';

const asset: Asset = {
	id: 'jcr:b3ee7444-4830-4454-abbb-20fc35387032',
	alt: 'image alt text',
	description: 'image description',
	name: 'someImage.jpg',
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

const container = testContainer().createChildContainer();
container.register(FlexibleImageUriFactory, { useClass: HashedFlexibleImageUriFactory });
const factory = container.resolve<FlexibleImageUriFactory>(FlexibleImageUriFactory);

describe('HashedFlexibleImageUriFactory', () => {
	test('create', () => {
		expect(factory.create(param)).toStrictEqual(
			'https://magnolia.mock/author/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/crop/true/hash/168818088/ratio/16:9/version/1733184000000/width/100/someImage.jpg',
		);
	});
});
