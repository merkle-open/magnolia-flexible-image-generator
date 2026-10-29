import { inject, injectable } from 'tsyringe';
import { FlexibleParameter, Parameter } from '../model/FlexibleParameter.ts';
import { FLEXIBLE_IMAGE_CONFIG_PROVIDER_TOKEN, type FlexibleImageConfigProviderI } from '../ConfigProvider.ts';
import { FlexibleImageUriFactory } from './FlexibleImageUriFactory.ts';
import { ImageDigest } from './ImageDigest.ts';

export interface HashedFlexibleParameter extends FlexibleParameter {
	hash: string;
}
export function DefaultHashedFlexibleParameterToMap(param: HashedFlexibleParameter, originalToMap: () => Parameter): Parameter {
	return {
		...originalToMap(),
		hash: param.hash,
	};
}

@injectable()
export class HashedFlexibleImageUriFactory extends FlexibleImageUriFactory {
	constructor(
		@inject(FLEXIBLE_IMAGE_CONFIG_PROVIDER_TOKEN) flexibleImageConfigProvider: FlexibleImageConfigProviderI,
		@inject(ImageDigest) private readonly imageDigest: ImageDigest,
	) {
		super(flexibleImageConfigProvider);
	}

	/*
	 * /<context>/.imaging/flex/assetItemKey/hash/hashValue/param1Key/param1Value/.../fileName
	 * e.g. /author/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/crop/true/hash/466cea7f608f4b1b59fee12ff43f7e68/ratio/16:9/version/1733184000000/width/100/someImage.jpg
	 */
	public create(data: FlexibleParameter): string {
		const uri = new URL(super.create(data)).pathname;
		const hashedFlexibleParameter: HashedFlexibleParameter = {
			...data,
			hash: this.imageDigest.getHash(uri),
			toMap: () => DefaultHashedFlexibleParameterToMap(hashedFlexibleParameter, data.toMap),
		};
		return super.create(hashedFlexibleParameter);
	}
}
