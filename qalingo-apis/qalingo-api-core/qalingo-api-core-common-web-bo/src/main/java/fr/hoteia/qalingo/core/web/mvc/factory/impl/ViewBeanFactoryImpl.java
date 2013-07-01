/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.7.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2013
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package fr.hoteia.qalingo.core.web.mvc.factory.impl;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.hoteia.qalingo.core.domain.AbstractPaymentGateway;
import fr.hoteia.qalingo.core.domain.AbstractRuleReferential;
import fr.hoteia.qalingo.core.domain.Asset;
import fr.hoteia.qalingo.core.domain.BatchProcessObject;
import fr.hoteia.qalingo.core.domain.CatalogCategoryMaster;
import fr.hoteia.qalingo.core.domain.CatalogCategoryMasterAttribute;
import fr.hoteia.qalingo.core.domain.CatalogCategoryVirtual;
import fr.hoteia.qalingo.core.domain.CatalogCategoryVirtualAttribute;
import fr.hoteia.qalingo.core.domain.CatalogMaster;
import fr.hoteia.qalingo.core.domain.CatalogVirtual;
import fr.hoteia.qalingo.core.domain.CurrencyReferential;
import fr.hoteia.qalingo.core.domain.Customer;
import fr.hoteia.qalingo.core.domain.EngineSetting;
import fr.hoteia.qalingo.core.domain.EngineSettingValue;
import fr.hoteia.qalingo.core.domain.Localization;
import fr.hoteia.qalingo.core.domain.Market;
import fr.hoteia.qalingo.core.domain.MarketArea;
import fr.hoteia.qalingo.core.domain.MarketPlace;
import fr.hoteia.qalingo.core.domain.Order;
import fr.hoteia.qalingo.core.domain.ProductAssociationLink;
import fr.hoteia.qalingo.core.domain.ProductBrand;
import fr.hoteia.qalingo.core.domain.ProductMarketing;
import fr.hoteia.qalingo.core.domain.ProductMarketingAttribute;
import fr.hoteia.qalingo.core.domain.ProductSku;
import fr.hoteia.qalingo.core.domain.ProductSkuAttribute;
import fr.hoteia.qalingo.core.domain.Retailer;
import fr.hoteia.qalingo.core.domain.Shipping;
import fr.hoteia.qalingo.core.domain.User;
import fr.hoteia.qalingo.core.domain.UserConnectionLog;
import fr.hoteia.qalingo.core.domain.UserGroup;
import fr.hoteia.qalingo.core.domain.UserPermission;
import fr.hoteia.qalingo.core.domain.UserRole;
import fr.hoteia.qalingo.core.i18n.enumtype.ScopeCommonMessage;
import fr.hoteia.qalingo.core.i18n.enumtype.ScopeReferenceDataMessage;
import fr.hoteia.qalingo.core.i18n.enumtype.ScopeWebMessage;
import fr.hoteia.qalingo.core.service.MarketPlaceService;
import fr.hoteia.qalingo.core.service.ProductMarketingService;
import fr.hoteia.qalingo.core.service.ProductSkuService;
import fr.hoteia.qalingo.core.web.cache.util.WebCacheHelper;
import fr.hoteia.qalingo.core.web.cache.util.WebElementType;
import fr.hoteia.qalingo.core.web.mvc.factory.ViewBeanFactory;
import fr.hoteia.qalingo.core.web.service.BackofficeUrlService;
import fr.hoteia.qalingo.core.web.util.RequestUtil;
import fr.hoteia.qalingo.web.mvc.viewbean.AssetViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.BatchViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.BrandViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.CatalogCategoryViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.CatalogViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.CommonViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.CurrencyReferentialViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.CustomerViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.EngineSettingDetailsViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.EngineSettingValueEditViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.EngineSettingValueViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.EngineSettingViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.FooterMenuViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.GlobalSearchViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.LegalTermsViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.LocalizationViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.MarketAreaViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.MarketPlaceViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.MarketViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.MenuViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.OrderViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.PaymentGatewayViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.ProductAssociationLinkViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.ProductMarketingViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.ProductSkuViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.QuickSearchViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.RetailerViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.RuleViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.SecurityViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.ShippingViewBean;
import fr.hoteia.qalingo.web.mvc.viewbean.UserConnectionLogValueBean;
import fr.hoteia.qalingo.web.mvc.viewbean.UserViewBean;

/**
 * 
 */
@Service("viewBeanFactory")
public class ViewBeanFactoryImpl extends AbstractBackofficeViewBeanFactory implements ViewBeanFactory {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
    protected RequestUtil requestUtil;
	
	@Autowired
    protected BackofficeUrlService backofficeUrlService;
	
	@Autowired
	protected MarketPlaceService marketPlaceService;
	
	@Autowired
	private ProductMarketingService productMarketingService;

	@Autowired
	private ProductSkuService productSkuService;
	
	@Resource(name="menuMarketNavigationCacheHelper")
    protected WebCacheHelper menuMarketNavigationCacheHelper;
	
	/**
	 * @throws Exception 
	 * 
	 */
	public CommonViewBean buildCommonViewBean(final HttpServletRequest request, final MarketPlace marketPlace, final Market market, final MarketArea marketArea, 
											  final Localization localization, final Retailer retailer) throws Exception {
		
		final CommonViewBean commonViewBean = new CommonViewBean();
		final String currentThemeResourcePrefixPath = requestUtil.getCurrentThemeResourcePrefixPath(request);
		commonViewBean.setThemeResourcePrefixPath(currentThemeResourcePrefixPath);

		commonViewBean.setHomeUrl(backofficeUrlService.buildHomeUrl());
		commonViewBean.setLoginUrl(backofficeUrlService.buildLoginUrl());
		commonViewBean.setLogoutUrl(backofficeUrlService.buildLogoutUrl());
		commonViewBean.setUserDetailsUrl(backofficeUrlService.buildUserDetailsUrl());
		commonViewBean.setCurrentMarketPlace(buildMarketPlaceViewBean(request, marketPlace));
		commonViewBean.setCurrentMarket(buildMarketViewBean(request, market));
		commonViewBean.setCurrentMarketArea(buildMarketAreaViewBean(request, marketArea));
		commonViewBean.setCurrentMarketLocalization(buildLocalizationViewBean(request, marketArea, localization));
		commonViewBean.setCurrentRetailer(buildRetailerViewBean(request, marketArea, localization, retailer));
		
		return commonViewBean;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public List<MenuViewBean> buildMenuViewBeans(final HttpServletRequest request, final Localization localization) throws Exception {
		return new ArrayList<MenuViewBean>();
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public List<MenuViewBean> buildMorePageMenuViewBeans(final HttpServletRequest request, final Localization localization) throws Exception {
		final List<MenuViewBean> menuViewBeans = new ArrayList<MenuViewBean>();
		
		// TODO : denis : move this part in the global list menu java/vm
		
		MenuViewBean menu = new MenuViewBean();
		menu.setCssIcon("icon-paper-clip");
		menu.setName("FAQ");
		menu.setUrl(backofficeUrlService.buildFaqUrl());
		menuViewBeans.add(menu);
		
		return menuViewBeans;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public List<FooterMenuViewBean> buildFooterMenuViewBeans(final HttpServletRequest request, final Localization localization) throws Exception {
		final Locale locale = localization.getLocale();
		
		final List<FooterMenuViewBean> footerMenuViewBeans = new ArrayList<FooterMenuViewBean>();
		
		FooterMenuViewBean footerMenuList = new FooterMenuViewBean();
		footerMenuList.setName(getSpecificMessage(ScopeWebMessage.HEADER_MENU, "home", locale));
		footerMenuList.setUrl(backofficeUrlService.buildHomeUrl());
		footerMenuViewBeans.add(footerMenuList);
		
		return footerMenuViewBeans;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public List<MarketPlaceViewBean> buildMarketPlaceViewBeans(final HttpServletRequest request, final Localization localization) throws Exception {
		final WebElementType marketPlaceElementType = WebElementType.MARKET_PLACE_NAVIGATION_VIEW_BEAN_LIST;
		final String marketPlacePrefixCacheKey = menuMarketNavigationCacheHelper.buildGlobalPrefixKey();
		final String marketPlaceCacheKey = marketPlacePrefixCacheKey + "_MARKETPLACE_LIST";
		List<MarketPlaceViewBean> marketPlaceViewBeans = (List<MarketPlaceViewBean>) menuMarketNavigationCacheHelper.getFromCache(marketPlaceElementType, marketPlaceCacheKey);
		if(marketPlaceViewBeans == null){
			marketPlaceViewBeans = new ArrayList<MarketPlaceViewBean>();
			final List<MarketPlace> marketPlaceList = marketPlaceService.findMarketPlaces();
			for (Iterator<MarketPlace> iteratorMarketPlace = marketPlaceList.iterator(); iteratorMarketPlace.hasNext();) {
				final MarketPlace marketPlaceNavigation = (MarketPlace) iteratorMarketPlace.next();
				marketPlaceViewBeans.add(buildMarketPlaceViewBean(request, marketPlaceNavigation));
			}
			menuMarketNavigationCacheHelper.addToCache(marketPlaceElementType, marketPlaceCacheKey, marketPlaceViewBeans);
		}
		return marketPlaceViewBeans;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public MarketPlaceViewBean buildMarketPlaceViewBean(final HttpServletRequest request, final MarketPlace marketPlace) throws Exception {
		final Market defaultMarket = marketPlace.getDefaultMarket();
		final MarketArea defaultMarketArea = defaultMarket.getDefaultMarketArea();
		final Localization defaultLocalization = defaultMarketArea.getDefaultLocalization();
		final Retailer defaultRetailer = defaultMarketArea.getDefaultRetailer();
		
		MarketPlaceViewBean marketPlaceViewBean = new MarketPlaceViewBean();
		marketPlaceViewBean.setName(marketPlace.getName());
		marketPlaceViewBean.setUrl(backofficeUrlService.buildChangeContextUrl(marketPlace, defaultMarket, defaultMarketArea, defaultLocalization, defaultRetailer));
		
		marketPlaceViewBean.setMarkets(buildMarketViewBeans(request, marketPlace, new ArrayList<Market>(marketPlace.getMarkets()), defaultLocalization));
		
		return marketPlaceViewBean;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public List<MarketViewBean> buildMarketViewBeans(final HttpServletRequest request, final MarketPlace marketPlace, final List<Market> markets, final Localization localization) throws Exception {
		final WebElementType marketElementType = WebElementType.MARKET_NAVIGATION_VIEW_BEAN_LIST;
		final String marketPrefixCacheKey = menuMarketNavigationCacheHelper.buildGlobalPrefixKey();
		final String marketCacheKey = marketPrefixCacheKey + "_" + marketPlace.getCode() + "_MARKET_LIST";
		List<MarketViewBean> marketViewBeans = (List<MarketViewBean>) menuMarketNavigationCacheHelper.getFromCache(marketElementType, marketCacheKey);
		if(marketViewBeans == null){
			marketViewBeans = new ArrayList<MarketViewBean>();
			for (Iterator<Market> iteratorMarket = markets.iterator(); iteratorMarket.hasNext();) {
				final Market marketNavigation = (Market) iteratorMarket.next();
				marketViewBeans.add(buildMarketViewBean(request, marketNavigation));
			}
			menuMarketNavigationCacheHelper.addToCache(marketElementType, marketCacheKey, marketViewBeans);
		}
		return marketViewBeans;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public MarketViewBean buildMarketViewBean(final HttpServletRequest request, final Market market) throws Exception {
		final MarketPlace marketPlace = market.getMarketPlace();
		final MarketArea defaultMarketArea = market.getDefaultMarketArea();
		final Localization defaultLocalization = defaultMarketArea.getDefaultLocalization();
		final Retailer defaultRetailer = defaultMarketArea.getDefaultRetailer();
		
		final MarketViewBean marketViewBean = new MarketViewBean();
		marketViewBean.setName(market.getName());
		marketViewBean.setUrl(backofficeUrlService.buildChangeContextUrl(marketPlace, market, defaultMarketArea, defaultLocalization, defaultRetailer));
		
		marketViewBean.setMarketAreas(buildMarketAreaViewBeans(request, market, new ArrayList<MarketArea>(market.getMarketAreas()), defaultLocalization));
		
		return marketViewBean;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public List<MarketAreaViewBean> buildMarketAreaViewBeans(final HttpServletRequest request, final Market market, final List<MarketArea> marketAreas, final Localization localization) throws Exception {
		final WebElementType marketAreaElementType = WebElementType.MARKET_AREA_VIEW_BEAN_LIST;
		final String marketAreaPrefixCacheKey = menuMarketNavigationCacheHelper.buildGlobalPrefixKey(localization);
		final String marketAreaCacheKey = marketAreaPrefixCacheKey + "_" + market.getCode() + "_MARKET_AREA_LIST";
		List<MarketAreaViewBean> marketAreaViewBeans = (List<MarketAreaViewBean>) menuMarketNavigationCacheHelper.getFromCache(marketAreaElementType, marketAreaCacheKey);
		if(marketAreaViewBeans == null){
			marketAreaViewBeans = new ArrayList<MarketAreaViewBean>();
			for (Iterator<MarketArea> iteratorMarketArea = marketAreas.iterator(); iteratorMarketArea.hasNext();) {
				final MarketArea marketArea = (MarketArea) iteratorMarketArea.next();
				marketAreaViewBeans.add(buildMarketAreaViewBean(request, marketArea));
			}
			menuMarketNavigationCacheHelper.addToCache(marketAreaElementType, marketAreaCacheKey, marketAreaViewBeans);
		}
		return marketAreaViewBeans;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public MarketAreaViewBean buildMarketAreaViewBean(final HttpServletRequest request, final MarketArea marketArea) throws Exception {
		final Market market = marketArea.getMarket();
		final MarketPlace marketPlace = market.getMarketPlace();
		final Localization defaultLocalization = marketArea.getDefaultLocalization();
		final Retailer defaultRetailer = marketArea.getDefaultRetailer();
		
		final MarketAreaViewBean marketAreaViewBean = new MarketAreaViewBean();
		marketAreaViewBean.setName(marketArea.getName());
		marketAreaViewBean.setUrl(backofficeUrlService.buildChangeContextUrl(marketPlace, market, marketArea, defaultLocalization, defaultRetailer));
		return marketAreaViewBean;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public List<LocalizationViewBean> buildLocalizationViewBeans(final HttpServletRequest request, final MarketArea marketArea, final Localization localization) throws Exception {
		final WebElementType localizationElementType = WebElementType.LOCALIZATION_VIEW_BEAN_LIST;
		final String localizationPrefixCacheKey = menuMarketNavigationCacheHelper.buildGlobalPrefixKey(localization);
		final String localizationCacheKey = localizationPrefixCacheKey + "_" + marketArea.getCode() + "_LOCALIZATION_LIST";
		List<LocalizationViewBean> localizationViewBeans = (List<LocalizationViewBean>) menuMarketNavigationCacheHelper.getFromCache(localizationElementType, localizationCacheKey);
		if(localizationViewBeans == null){
			final List<Localization> translationAvailables = new ArrayList<Localization>(marketArea.getLocalizations());
			localizationViewBeans = new ArrayList<LocalizationViewBean>();
			for (Iterator<Localization> iterator = translationAvailables.iterator(); iterator.hasNext();) {
				final Localization localizationAvailable = (Localization) iterator.next();
				localizationViewBeans.add(buildLocalizationViewBean(request, marketArea, localizationAvailable));
			}
			menuMarketNavigationCacheHelper.addToCache(localizationElementType, localizationCacheKey, localizationViewBeans);
		}
		return localizationViewBeans;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public LocalizationViewBean buildLocalizationViewBean(final HttpServletRequest request, final MarketArea marketArea, final Localization localization) throws Exception {
		final Market market = marketArea.getMarket();
		final MarketPlace marketPlace = market.getMarketPlace();
		final Locale locale = localization.getLocale();
		final String localeCodeNavigation = localization.getCode();
		final Retailer retailer = requestUtil.getCurrentRetailer(request);
		
		final LocalizationViewBean localizationViewBean = new LocalizationViewBean();
		
		if(StringUtils.isNotEmpty(localeCodeNavigation)
				&& localeCodeNavigation.length() == 2) {
			localizationViewBean.setName(getReferenceData(ScopeReferenceDataMessage.LANGUAGE, localeCodeNavigation.toLowerCase(), locale));
		} else {
			localizationViewBean.setName(getReferenceData(ScopeReferenceDataMessage.LANGUAGE, localeCodeNavigation, locale));
		}
		
		localizationViewBean.setUrl(backofficeUrlService.buildChangeContextUrl(marketPlace, market, marketArea, localization, retailer));
		return localizationViewBean;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public List<LocalizationViewBean> buildLocalizationViewBeans(final HttpServletRequest request, final List<Localization> localizations) throws Exception {
		final List<LocalizationViewBean> localizationViewBeans = new ArrayList<LocalizationViewBean>();
		if(localizations != null){
			for (Iterator<Localization> iterator = localizations.iterator(); iterator.hasNext();) {
				Localization localization = (Localization) iterator.next();
				localizationViewBeans.add(buildLocalizationViewBean(request, localization));
			}
		}
		return localizationViewBeans;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public LocalizationViewBean buildLocalizationViewBean(final HttpServletRequest request, final Localization localization) throws Exception {
		final Locale locale = localization.getLocale();
		final String localeCodeNavigation = localization.getCode();
		
		final LocalizationViewBean localizationViewBean = new LocalizationViewBean();
		
		if(StringUtils.isNotEmpty(localeCodeNavigation)
				&& localeCodeNavigation.length() == 2) {
			localizationViewBean.setName(getReferenceData(ScopeReferenceDataMessage.LANGUAGE, localeCodeNavigation.toLowerCase(), locale));
		} else {
			localizationViewBean.setName(getReferenceData(ScopeReferenceDataMessage.LANGUAGE, localeCodeNavigation, locale));
		}
		
		localizationViewBean.setUrl(backofficeUrlService.buildChangeLanguageUrl(localization));
		return localizationViewBean;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public List<RetailerViewBean> buildRetailerViewBeans(final HttpServletRequest request, final MarketArea marketArea, final Localization localization) throws Exception {
		final WebElementType retailerElementType = WebElementType.RETAILER_VIEW_BEAN_LIST;
		final String retailerPrefixCacheKey = menuMarketNavigationCacheHelper.buildGlobalPrefixKey(localization);
		final String retailerCacheKey = retailerPrefixCacheKey + "_RETAILER";
		List<RetailerViewBean> retailerViewBeans = (List<RetailerViewBean>) menuMarketNavigationCacheHelper.getFromCache(retailerElementType, retailerCacheKey);
		if(retailerViewBeans == null){
			final List<Retailer> retailers = new ArrayList<Retailer>(marketArea.getRetailers());
			retailerViewBeans = new ArrayList<RetailerViewBean>();
			for (Iterator<Retailer> iterator = retailers.iterator(); iterator.hasNext();) {
				final Retailer retailer = (Retailer) iterator.next();
				retailerViewBeans.add(buildRetailerViewBean(request, marketArea, localization, retailer));
			}
			menuMarketNavigationCacheHelper.addToCache(retailerElementType, retailerCacheKey, retailerViewBeans);
		}
		return retailerViewBeans;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public RetailerViewBean buildRetailerViewBean(final HttpServletRequest request, final MarketArea marketArea, final Localization localization, final Retailer retailer) throws Exception {
		final Market market = marketArea.getMarket();
		final MarketPlace marketPlace = market.getMarketPlace();
		final RetailerViewBean retailerViewBean = new RetailerViewBean();
		retailerViewBean.setName(retailer.getName());
		retailerViewBean.setUrl(backofficeUrlService.buildChangeContextUrl(marketPlace, market, marketArea, localization, retailer));
		return retailerViewBean;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public CatalogViewBean buildMasterCatalogViewBean(final HttpServletRequest request, final MarketArea marketArea, final Localization localization, 
													  final CatalogMaster catalogMaster, final List<CatalogCategoryMaster> productCategories) throws Exception {
		final CatalogViewBean catalogViewBean = new CatalogViewBean();
		catalogViewBean.setBusinessName(catalogMaster.getBusinessName());
		catalogViewBean.setCode(catalogMaster.getCode());
		
		if(productCategories != null){
			catalogViewBean.setCategories(buildMasterProductCategoryViewBeans(request, marketArea, localization, productCategories, true));
		}

		catalogViewBean.setAddRootCategoryUrl(backofficeUrlService.buildAddMasterProductCategoryUrl(null));
//		catalogViewBean.setAddRootCategoryUrlLabel(getSpecificMessage("business.catalog.add.master.category.label", locale));

		return catalogViewBean;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public CatalogViewBean buildVirtualCatalogViewBean(final HttpServletRequest request, final MarketArea marketArea, final Localization localization, final CatalogVirtual catalogVirtual, final List<CatalogCategoryVirtual> productCategories) throws Exception {
		final CatalogViewBean catalogViewBean = new CatalogViewBean();
		catalogViewBean.setBusinessName(catalogVirtual.getBusinessName());
		catalogViewBean.setCode(catalogVirtual.getCode());
		
		if(productCategories != null){
			catalogViewBean.setCategories(buildVirtualProductCategoryViewBeans(request, marketArea, localization, productCategories, true));
		}

		catalogViewBean.setAddRootCategoryUrl(backofficeUrlService.buildAddVirtualProductCategoryUrl(null));
//		catalogViewBean.setAddRootCategoryUrlLabel(getSpecificMessage("business.catalog.add.virtual.category.label", locale));

		return catalogViewBean;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public List<CatalogCategoryViewBean> buildMasterProductCategoryViewBeans(final HttpServletRequest request, final MarketArea marketArea, final Localization localization, 
																			 final List<CatalogCategoryMaster> productCategories, boolean fullPopulate) throws Exception {
		List<CatalogCategoryViewBean> categoryViewBeans = new ArrayList<CatalogCategoryViewBean>();
		for (Iterator<CatalogCategoryMaster> iterator = productCategories.iterator(); iterator.hasNext();) {
			final CatalogCategoryMaster category = (CatalogCategoryMaster) iterator.next();
			categoryViewBeans.add(buildMasterProductCategoryViewBean(request, marketArea, localization, category, fullPopulate));
		}
		return categoryViewBeans;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public List<CatalogCategoryViewBean> buildVirtualProductCategoryViewBeans(final HttpServletRequest request, final MarketArea marketArea, final Localization localization, 
																			  final List<CatalogCategoryVirtual> productCategories, boolean fullPopulate) throws Exception {
		List<CatalogCategoryViewBean> categoryViewBeans = new ArrayList<CatalogCategoryViewBean>();
		for (Iterator<CatalogCategoryVirtual> iterator = productCategories.iterator(); iterator.hasNext();) {
			final CatalogCategoryVirtual category = (CatalogCategoryVirtual) iterator.next();
			categoryViewBeans.add(buildVirtualProductCategoryViewBean(request, marketArea, localization, category, fullPopulate));
		}
		return categoryViewBeans;
	}

	/**
	 * @throws Exception 
	 * 
	 */
	public CatalogCategoryViewBean buildMasterProductCategoryViewBean(final HttpServletRequest request, final MarketArea marketArea, 
																	  final Localization localization, final CatalogCategoryMaster category, boolean fullPopulate) throws Exception {
		final CatalogCategoryViewBean productCategoryViewBean = new CatalogCategoryViewBean();
		
//		// VIEW/FORM LABELS
//		productCategoryViewBean.setBusinessNameLabel(getSpecificMessage("business.catalog.category.details.business.name.label", locale));
//		productCategoryViewBean.setBusinessNameInformation(getSpecificMessage("business.catalog.category.details.business.name.information", locale));
//		productCategoryViewBean.setDescriptionLabel(getSpecificMessage("business.catalog.category.details.description.label", locale));
//		productCategoryViewBean.setDescriptionInformation(getSpecificMessage("business.catalog.category.details.description.information", locale));
//		productCategoryViewBean.setIsDefaultLabel(getSpecificMessage("business.catalog.category.details.is.default.label", locale));
//		productCategoryViewBean.setCodeLabel(getSpecificMessage("business.catalog.category.details.code.label", locale));
//		productCategoryViewBean.setDefaultParentCategoryLabel(getSpecificMessage("business.catalog.category.details.default.parent.category.label", locale));
//		productCategoryViewBean.setProductBrandLabel(getSpecificMessage("business.catalog.category.details.product.brand.label", locale));
//		productCategoryViewBean.setProductMarketingGlobalAttributesLabel(getSpecificMessage("business.catalog.category.details.global.attribute.list.label", locale)); 
//		productCategoryViewBean.setProductMarketingMarketAreaAttributesLabel(getSpecificMessage("business.catalog.category.details.area.attribute.list.label", locale)); 
//		productCategoryViewBean.setProductMarketingLabel(getSpecificMessage("business.catalog.category.details.product.marketing.list.label", locale));
//		productCategoryViewBean.setSubCategoriesLabel(getSpecificMessage("business.catalog.category.details.sub.category.list.label", locale));
//		productCategoryViewBean.setDateCreateLabel(getSpecificMessage("business.catalog.category.details.created.date.label", locale));
//		productCategoryViewBean.setDateUpdateLabel(getSpecificMessage("business.catalog.category.details.updated.date.label", locale));

		if(category != null){
			final String categoryCode = category.getCode();

			productCategoryViewBean.setBusinessName(category.getBusinessName());
			productCategoryViewBean.setCode(categoryCode);
			productCategoryViewBean.setDescription(category.getDescription());
			
			if(category.getDefaultParentCatalogCategory() != null){
				productCategoryViewBean.setDefaultParentCategory(buildMasterProductCategoryViewBean(request, marketArea, localization, category.getDefaultParentCatalogCategory(), false));
			}
			
			DateFormat dateFormat = requestUtil.getFormatDate(request, DateFormat.MEDIUM, DateFormat.MEDIUM);
			Date createdDate = category.getDateCreate();
			if(createdDate != null){
				productCategoryViewBean.setCreatedDate(dateFormat.format(createdDate));
			} else {
				productCategoryViewBean.setCreatedDate("NA");
			}
			Date updatedDate = category.getDateUpdate();
			if(updatedDate != null){
				productCategoryViewBean.setUpdatedDate(dateFormat.format(updatedDate));
			} else {
				productCategoryViewBean.setUpdatedDate("NA");
			}

			if(fullPopulate){
				if(category.getCatalogCategories() != null){
					productCategoryViewBean.setSubCategories(buildMasterProductCategoryViewBeans(request, marketArea, localization, new ArrayList<CatalogCategoryMaster>(category.getCatalogCategories()), fullPopulate));
				}
				
				Set<CatalogCategoryMasterAttribute> globalAttributes = category.getCatalogCategoryGlobalAttributes();
				for (Iterator<CatalogCategoryMasterAttribute> iterator = globalAttributes.iterator(); iterator.hasNext();) {
					CatalogCategoryMasterAttribute productCategoryMasterAttribute = (CatalogCategoryMasterAttribute) iterator.next();
					productCategoryViewBean.getGlobalAttributes().put(productCategoryMasterAttribute.getAttributeDefinition().getCode(), productCategoryMasterAttribute.getValueAsString());
				}
				
				Set<CatalogCategoryMasterAttribute> marketAreaAttributes = category.getCatalogCategoryMarketAreaAttributes();
				for (Iterator<CatalogCategoryMasterAttribute> iterator = marketAreaAttributes.iterator(); iterator.hasNext();) {
					CatalogCategoryMasterAttribute productCategoryMasterAttribute = (CatalogCategoryMasterAttribute) iterator.next();
					productCategoryViewBean.getMarketAreaAttributes().put(productCategoryMasterAttribute.getAttributeDefinition().getCode(), productCategoryMasterAttribute.getValueAsString());
				}
				
				List<ProductMarketingViewBean> productMarketingViewBeans = buildProductMarketingViewBeans(request, marketArea, localization, new ArrayList<ProductMarketing>(category.getProductMarketings()), true);
				productCategoryViewBean.setProductMarketings(productMarketingViewBeans);

				int countProduct = category.getProductMarketings().size();
				for (Iterator<CatalogCategoryViewBean> iterator = productCategoryViewBean.getSubCategories().iterator(); iterator.hasNext();) {
					CatalogCategoryViewBean subCategoryViewBean = (CatalogCategoryViewBean) iterator.next();
					countProduct = countProduct + subCategoryViewBean.getCountProduct();
				}
				productCategoryViewBean.setCountProduct(countProduct);
				
				Set<Asset> assets = category.getAssetsIsGlobal();
				for (Iterator<Asset> iterator = assets.iterator(); iterator.hasNext();) {
					Asset asset = (Asset) iterator.next();
					productCategoryViewBean.getAssets().add(buildAssetViewBean(request, marketArea, localization, asset));
				}
			}

//			productCategoryViewBean.setCategoryDetailsLabel(getSpecificMessage("business.catalog.category.details.url.label", locale));
			productCategoryViewBean.setCategoryDetailsUrl(backofficeUrlService.buildProductMasterCategoryDetailsUrl(categoryCode));

//			productCategoryViewBean.setCategoryEditLabel(getSpecificMessage("business.catalog.category.edit.url.label", locale));
			productCategoryViewBean.setCategoryEditUrl(backofficeUrlService.buildMasterProductCategoryEditUrl(categoryCode));
		}
		
//		productCategoryViewBean.setSubmitLabel(getSpecificMessage("business.catalog.category.edit.submit.label", locale));
		productCategoryViewBean.setFormSubmitUrl(backofficeUrlService.buildMasterProductCategoryFormPostUrl());

//		productCategoryViewBean.setCancelLabel(getSpecificMessage("business.catalog.category.edit.cancel.label", locale));
		final List<String> excludedPatterns = new ArrayList<String>();
		excludedPatterns.add("form");
		productCategoryViewBean.setBackUrl(requestUtil.getLastRequestUrl(request, excludedPatterns));

		return productCategoryViewBean;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public CatalogCategoryViewBean buildVirtualProductCategoryViewBean(final HttpServletRequest request, final MarketArea marketArea, 
																	   final Localization localization, final CatalogCategoryVirtual category, boolean fullPopulate) throws Exception {
		final CatalogCategoryViewBean catalogCategoryViewBean = new CatalogCategoryViewBean();
		
//		// VIEW/FORM LABELS
//		catalogCategoryViewBean.setBusinessNameLabel(getSpecificMessage("business.catalog.category.details.business.name.label", locale));
//		catalogCategoryViewBean.setBusinessNameInformation(getSpecificMessage("business.catalog.category.details.business.name.information", locale));
//		catalogCategoryViewBean.setDescriptionLabel(getSpecificMessage("business.catalog.category.details.description.label", locale));
//		catalogCategoryViewBean.setDescriptionInformation(getSpecificMessage("business.catalog.category.details.description.information", locale));
//		catalogCategoryViewBean.setIsDefaultLabel(getSpecificMessage("business.catalog.category.details.is.default.label", locale));
//		catalogCategoryViewBean.setCodeLabel(getSpecificMessage("business.catalog.category.details.code.label", locale));
//		catalogCategoryViewBean.setDefaultParentCategoryLabel(getSpecificMessage("business.catalog.category.details.default.parent.category.label", locale));
//		catalogCategoryViewBean.setProductBrandLabel(getSpecificMessage("business.catalog.category.details.product.brand.label", locale));
//		catalogCategoryViewBean.setProductMarketingGlobalAttributesLabel(getSpecificMessage("business.catalog.category.details.global.attribute.list.label", locale)); 
//		catalogCategoryViewBean.setProductMarketingMarketAreaAttributesLabel(getSpecificMessage("business.catalog.category.details.area.attribute.list.label", locale)); 
//		catalogCategoryViewBean.setProductMarketingLabel(getSpecificMessage("business.catalog.category.details.product.marketing.list.label", locale));
//		catalogCategoryViewBean.setSubCategoriesLabel(getSpecificMessage("business.catalog.category.details.sub.category.list.label", locale));
//		catalogCategoryViewBean.setDateCreateLabel(getSpecificMessage("business.catalog.category.details.created.date.label", locale));
//		catalogCategoryViewBean.setDateUpdateLabel(getSpecificMessage("business.catalog.category.details.updated.date.label", locale));

		if(category != null){
			final String categoryCode = category.getCode();

			catalogCategoryViewBean.setBusinessName(category.getBusinessName());
			catalogCategoryViewBean.setCode(categoryCode);
//			catalogCategoryViewBean.setDescriptionInformation(category.getDescription());

			if(category.getDefaultParentCatalogCategory() != null){
				catalogCategoryViewBean.setDefaultParentCategory(buildVirtualProductCategoryViewBean(request, marketArea, localization, category.getDefaultParentCatalogCategory(), false));
			}
			
			DateFormat dateFormat = requestUtil.getFormatDate(request, DateFormat.MEDIUM, DateFormat.MEDIUM);
			Date createdDate = category.getDateCreate();
			if(createdDate != null){
				catalogCategoryViewBean.setCreatedDate(dateFormat.format(createdDate));
			} else {
				catalogCategoryViewBean.setCreatedDate("NA");
			}
			Date updatedDate = category.getDateUpdate();
			if(updatedDate != null){
				catalogCategoryViewBean.setUpdatedDate(dateFormat.format(updatedDate));
			} else {
				catalogCategoryViewBean.setUpdatedDate("NA");
			}

			if(fullPopulate){
				if(category.getCatalogCategories() != null){
					catalogCategoryViewBean.setSubCategories(buildVirtualProductCategoryViewBeans(request, marketArea, localization, new ArrayList<CatalogCategoryVirtual>(category.getCatalogCategories()), fullPopulate));
				}

				Set<CatalogCategoryVirtualAttribute> globalAttributes = category.getCatalogCategoryGlobalAttributes();
				for (Iterator<CatalogCategoryVirtualAttribute> iterator = globalAttributes.iterator(); iterator.hasNext();) {
					CatalogCategoryVirtualAttribute productCategoryVirtualAttribute = (CatalogCategoryVirtualAttribute) iterator.next();
					catalogCategoryViewBean.getGlobalAttributes().put(productCategoryVirtualAttribute.getAttributeDefinition().getCode(), productCategoryVirtualAttribute.getValueAsString());
				}
				
				Set<CatalogCategoryVirtualAttribute> marketAreaAttributes = category.getCatalogCategoryMarketAreaAttributes();
				for (Iterator<CatalogCategoryVirtualAttribute> iterator = marketAreaAttributes.iterator(); iterator.hasNext();) {
					CatalogCategoryVirtualAttribute productCategoryVirtualAttribute = (CatalogCategoryVirtualAttribute) iterator.next();
					catalogCategoryViewBean.getMarketAreaAttributes().put(productCategoryVirtualAttribute.getAttributeDefinition().getCode(), productCategoryVirtualAttribute.getValueAsString());
				}
	
				List<ProductMarketingViewBean> productMarketingViewBeans = buildProductMarketingViewBeans(request, marketArea, localization, new ArrayList<ProductMarketing>(category.getProductMarketings()), true);
				catalogCategoryViewBean.setProductMarketings(productMarketingViewBeans);
				
				int countProduct = category.getProductMarketings().size();
				for (Iterator<CatalogCategoryViewBean> iterator = catalogCategoryViewBean.getSubCategories().iterator(); iterator.hasNext();) {
					CatalogCategoryViewBean subCategoryViewBean = (CatalogCategoryViewBean) iterator.next();
					countProduct = countProduct + subCategoryViewBean.getCountProduct();
				}
				catalogCategoryViewBean.setCountProduct(countProduct);
				
				Set<Asset> assets = category.getAssetsIsGlobal();
				for (Iterator<Asset> iterator = assets.iterator(); iterator.hasNext();) {
					Asset asset = (Asset) iterator.next();
					catalogCategoryViewBean.getAssets().add(buildAssetViewBean(request, marketArea, localization, asset));
				}
			}

//			catalogCategoryViewBean.setCategoryDetailsLabel(getSpecificMessage("business.catalog.category.details.url.label", locale));
			catalogCategoryViewBean.setCategoryDetailsUrl(backofficeUrlService.buildProductVirtualCategoryDetailsUrl(categoryCode));

//			catalogCategoryViewBean.setCategoryEditLabel(getSpecificMessage("business.catalog.category.edit.url.label", locale));
			catalogCategoryViewBean.setCategoryEditUrl(backofficeUrlService.buildVirtualProductCategoryEditUrl(categoryCode));
		}
		
//		catalogCategoryViewBean.setCancelLabel(getSpecificMessage("business.catalog.category.edit.cancel.label", locale));
		final List<String> excludedPatterns = new ArrayList<String>();
		excludedPatterns.add("form");
		catalogCategoryViewBean.setBackUrl(requestUtil.getLastRequestUrl(request, excludedPatterns));

//		catalogCategoryViewBean.setSubmitLabel(getSpecificMessage("business.catalog.category.edit.submit.label", locale));
		catalogCategoryViewBean.setFormSubmitUrl(backofficeUrlService.buildVirtualProductCategoryFormPostUrl());
		
		return catalogCategoryViewBean;
	}

	/**
	 * @throws Exception 
	 * 
	 */
	public List<ProductMarketingViewBean> buildProductMarketingViewBeans(HttpServletRequest request, MarketArea marketArea, Localization localization, 
																		 List<ProductMarketing> productMarketings, boolean withDependency) throws Exception {
		List<ProductMarketingViewBean> products = new ArrayList<ProductMarketingViewBean>();
		for (Iterator<ProductMarketing> iterator = productMarketings.iterator(); iterator.hasNext();) {
			ProductMarketing productMarketing = (ProductMarketing) iterator.next();
			products.add(buildProductMarketingViewBean(request, marketArea, localization, productMarketing, withDependency));
		}
		return products;
	}

	/**
	 * @throws Exception 
	 * 
	 */
	public ProductMarketingViewBean buildProductMarketingViewBean(HttpServletRequest request, MarketArea marketArea, Localization localization, 
																  ProductMarketing productMarketing, boolean withDependency) throws Exception {
		final Locale locale = localization.getLocale();
		final Long marketAreaId = marketArea.getId();
		final String productCode = productMarketing.getCode();
		
		final ProductMarketingViewBean productMarketingViewBean = new ProductMarketingViewBean();

//		// VIEW/FORM LABELS
//		productMarketingViewBean.setBusinessNameLabel(getSpecificMessage("business.product.marketing.details.business.name.label", locale));
//		productMarketingViewBean.setBusinessNameInformation(getSpecificMessage("business.product.marketing.details.business.name.information", locale));
//		productMarketingViewBean.setDescriptionLabel(getSpecificMessage("business.product.marketing.details.description.label", locale));
//		productMarketingViewBean.setDescriptionInformation(getSpecificMessage("business.product.marketing.details.description.information", locale));
//		productMarketingViewBean.setIsDefaultLabel(getSpecificMessage("business.product.marketing.details.is.default.label", locale));
//		productMarketingViewBean.setCodeLabel(getSpecificMessage("business.product.marketing.details.code.label", locale));
//		productMarketingViewBean.setProductBrandLabel(getSpecificMessage("business.product.marketing.details.product.brand.label", locale));
//		productMarketingViewBean.setProductMarketingGlobalAttributesLabel(getSpecificMessage("business.product.marketing.details.global.attribute.list.label", locale)); 
//		productMarketingViewBean.setProductMarketingMarketAreaAttributesLabel(getSpecificMessage("business.product.marketing.details.area.attribute.list.label", locale)); 
//		productMarketingViewBean.setProductSkusLabel(getSpecificMessage("business.product.marketing.details.sku.list.label", locale));
//		productMarketingViewBean.setProductCrossLinksLabel(getSpecificMessage("business.product.marketing.details.cross.product.list.label", locale));
//		productMarketingViewBean.setAssetsLabel(getSpecificMessage("business.product.marketing.details.asset.list.label", locale)); 
//		productMarketingViewBean.setDateCreateLabel(getSpecificMessage("business.product.marketing.details.created.date.label", locale));
//		productMarketingViewBean.setDateUpdateLabel(getSpecificMessage("business.product.marketing.details.updated.date.label", locale));
		
		Integer position = productMarketing.getOrder(marketAreaId);
		if(position != null){
			productMarketingViewBean.setPositionItem(position);
		}
		productMarketingViewBean.setBusinessName(productMarketing.getBusinessName());
		productMarketingViewBean.setCode(productMarketing.getCode());
		productMarketingViewBean.setDescription(productMarketing.getDescription());
		productMarketingViewBean.setDefault(productMarketing.isDefault());
		productMarketingViewBean.setBrand(buildBrandViewBean(request, productMarketing.getProductBrand()));

		Set<ProductMarketingAttribute> globalAttributes = productMarketing.getProductMarketingGlobalAttributes();
		for (Iterator<ProductMarketingAttribute> iterator = globalAttributes.iterator(); iterator.hasNext();) {
			ProductMarketingAttribute productMarketingAttribute = (ProductMarketingAttribute) iterator.next();
			productMarketingViewBean.getGlobalAttributes().put(productMarketingAttribute.getAttributeDefinition().getCode(), productMarketingAttribute.getValueAsString());
		}
		
		Set<ProductMarketingAttribute> marketAreaAttributes = productMarketing.getProductMarketingMarketAreaAttributes();
		for (Iterator<ProductMarketingAttribute> iterator = marketAreaAttributes.iterator(); iterator.hasNext();) {
			ProductMarketingAttribute productMarketingAttribute = (ProductMarketingAttribute) iterator.next();
			productMarketingViewBean.getMarketAreaAttributes().put(productMarketingAttribute.getAttributeDefinition().getCode(), productMarketingAttribute.getValueAsString());
		}
		
		if(withDependency){
			Set<ProductSku> productSkus = productMarketing.getProductSkus();
			for (Iterator<ProductSku> iterator = productSkus.iterator(); iterator.hasNext();) {
				ProductSku productSku = (ProductSku) iterator.next();
				productMarketingViewBean.getProductSkus().add(buildProductSkuViewBean(request, marketArea, localization, productSku));
			}
			
			Set<ProductAssociationLink> productCrossLinks = productMarketing.getProductAssociationLinks();
			for (Iterator<ProductAssociationLink> iterator = productCrossLinks.iterator(); iterator.hasNext();) {
				ProductAssociationLink productAssociationLink = (ProductAssociationLink) iterator.next();
				productMarketingViewBean.getProductAssociationLinks().add(buildProductAssociationLinkViewBean(request, marketArea, localization, productAssociationLink));
			}
			
			Set<Asset> assets = productMarketing.getAssetsIsGlobal();
			for (Iterator<Asset> iterator = assets.iterator(); iterator.hasNext();) {
				Asset asset = (Asset) iterator.next();
				productMarketingViewBean.getAssets().add(buildAssetViewBean(request, marketArea, localization, asset));
			}

		}
		
		DateFormat dateFormat = requestUtil.getFormatDate(request, DateFormat.MEDIUM, DateFormat.MEDIUM);
		Date createdDate = productMarketing.getDateCreate();
		if(createdDate != null){
			productMarketingViewBean.setCreatedDate(dateFormat.format(createdDate));
		} else {
			productMarketingViewBean.setCreatedDate("NA");
		}
		Date updatedDate = productMarketing.getDateUpdate();
		if(updatedDate != null){
			productMarketingViewBean.setUpdatedDate(dateFormat.format(updatedDate));
		} else {
			productMarketingViewBean.setUpdatedDate("NA");
		}
		
//		productMarketingViewBean.setBackgroundImage(productMarketing.getbackgroundImage);
//		productMarketingViewBean.setCarouselImage(productMarketing.getcarouselImage);
//		productMarketingViewBean.setIconImage(productMarketing.geticonImage);

//		productMarketingViewBean.setProductDetailsLabel(getSpecificMessage(ScopeWebMessage.PRODUCT_MARKETING, "business.product.marketing.details.url.label", locale));
		productMarketingViewBean.setProductDetailsUrl(backofficeUrlService.buildProductMarketingDetailsUrl(productCode));

//		productMarketingViewBean.setProductEditLabel(getSpecificMessage(ScopeWebMessage.PRODUCT_MARKETING, "business.product.marketing.edit.url.label", locale));
		productMarketingViewBean.setProductEditUrl(backofficeUrlService.buildProductMarketingEditUrl(productCode));

//		productMarketingViewBean.setCancelLabel(getSpecificMessage(ScopeWebMessage.PRODUCT_MARKETING, "business.product.marketing.edit.cancel.label", locale));
		final List<String> excludedPatterns = new ArrayList<String>();
		excludedPatterns.add("form");
		productMarketingViewBean.setBackUrl(requestUtil.getLastRequestUrl(request, excludedPatterns));

//		productMarketingViewBean.setSubmitLabel(getSpecificMessage(ScopeWebMessage.PRODUCT_MARKETING, "business.product.marketing.edit.submit.label", locale));
		productMarketingViewBean.setFormSubmitUrl(backofficeUrlService.buildProductMarketingEditUrl(productCode));
		
		return productMarketingViewBean;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public ProductAssociationLinkViewBean buildProductAssociationLinkViewBean(HttpServletRequest request, MarketArea marketArea, Localization localization, 
																			  ProductAssociationLink productAssociationLink) throws Exception {
		final Long marketAreaId = marketArea.getId();
		
		final ProductAssociationLinkViewBean productCrossLinkViewBean = new ProductAssociationLinkViewBean();
		
		productCrossLinkViewBean.setOrderItem(productAssociationLink.getRankPosition());
		productCrossLinkViewBean.setName(productAssociationLink.getProductMarketing().getBusinessName());
		productCrossLinkViewBean.setType(productAssociationLink.getType().name());
		productCrossLinkViewBean.setDescription(productAssociationLink.getType().name());
		final String productCode = productAssociationLink.getProductMarketing().getCode();
		productCrossLinkViewBean.setProductDetailsUrl(backofficeUrlService.buildProductMarketingDetailsUrl(productCode));
		
		return productCrossLinkViewBean;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public ProductSkuViewBean buildProductSkuViewBean(HttpServletRequest request, MarketArea marketArea, Localization localization, ProductSku productSku) throws Exception {
		final Long marketAreaId = marketArea.getId();
		final String productSkuCode = productSku.getCode();
		
		final ProductSkuViewBean productSkuViewBean = new ProductSkuViewBean();

//		// VIEW/FORM LABELS
//		productSkuViewBean.setBusinessNameLabel(getSpecificMessage("business.product.sku.details.business.name.label", locale));
//		productSkuViewBean.setBusinessNameInformation(getSpecificMessage("business.product.sku.details.business.name.information", locale));
//		productSkuViewBean.setDescriptionLabel(getSpecificMessage("business.product.sku.details.description.label", locale));
//		productSkuViewBean.setDescriptionInformation(getSpecificMessage("business.product.sku.details.description.information", locale));
//		productSkuViewBean.setIsDefaultLabel(getSpecificMessage("business.product.sku.details.is.default.label", locale));
//		productSkuViewBean.setCodeLabel(getSpecificMessage("business.product.sku.details.code.label", locale));
//		productSkuViewBean.setProductSkuGlobalAttributesLabel(getSpecificMessage("business.product.marketing.details.global.attribute.list.label", locale)); 
//		productSkuViewBean.setProductSkuMarketAreaAttributesLabel(getSpecificMessage("business.product.marketing.details.area.attribute.list.label", locale)); 
//
//		productSkuViewBean.setProductMarketingAssociatedLabel(getSpecificMessage("business.product.sku.details.product.marketing.associated.label", locale));
//		productSkuViewBean.setProductBrandLabel(getSpecificMessage("business.product.sku.details.product.brand.label", locale));
//		productSkuViewBean.setProductMarketingGlobalAttributesLabel(getSpecificMessage("business.product.marketing.details.global.attribute.list.label", locale)); 
//		productSkuViewBean.setProductMarketingMarketAreaAttributesLabel(getSpecificMessage("business.product.marketing.details.area.attribute.list.label", locale)); 
//		productSkuViewBean.setProductSkusLabel(getSpecificMessage("business.product.sku.details.sku.list.label", locale));
//		productSkuViewBean.setProductCrossLinksLabel(getSpecificMessage("business.product.sku.details.cross.product.list.label", locale));
//		productSkuViewBean.setAssetsLabel(getSpecificMessage("business.product.sku.details.asset.list.label", locale)); 
//		productSkuViewBean.setDateCreateLabel(getSpecificMessage("business.product.sku.details.created.date.label", locale));
//		productSkuViewBean.setDateUpdateLabel(getSpecificMessage("business.product.sku.details.updated.date.label", locale));
		
		Integer position = productSku.getOrder(marketAreaId);
		if(position != null){
			productSkuViewBean.setPositionItem(position);
		}
		productSkuViewBean.setBusinessName(productSku.getBusinessName());
		productSkuViewBean.setCode(productSku.getCode());
		productSkuViewBean.setDescription(productSku.getDescription());
		productSkuViewBean.setDefault(productSku.isDefault());

		Set<ProductSkuAttribute> skuGlobalAttributes = productSku.getProductSkuGlobalAttributes();
		for (Iterator<ProductSkuAttribute> iterator = skuGlobalAttributes.iterator(); iterator.hasNext();) {
			ProductSkuAttribute productSkuAttribute = (ProductSkuAttribute) iterator.next();
			productSkuViewBean.getGlobalAttributes().put(productSkuAttribute.getAttributeDefinition().getCode(), productSkuAttribute.getValueAsString());
		}
		
		Set<ProductSkuAttribute> skuMarketAreaAttributes = productSku.getProductSkuMarketAreaAttributes();
		for (Iterator<ProductSkuAttribute> iterator = skuMarketAreaAttributes.iterator(); iterator.hasNext();) {
			ProductSkuAttribute productSkuAttribute = (ProductSkuAttribute) iterator.next();
			productSkuViewBean.getMarketAreaAttributes().put(productSkuAttribute.getAttributeDefinition().getCode(), productSkuAttribute.getValueAsString());
		}
		
		productSkuViewBean.setProductMarketing(buildProductMarketingViewBean(request, marketArea, localization, productSku.getProductMarketing(), false));

		Set<ProductMarketingAttribute> productMarketingGlobalAttributes = productSku.getProductMarketing().getProductMarketingGlobalAttributes();
		for (Iterator<ProductMarketingAttribute> iterator = productMarketingGlobalAttributes.iterator(); iterator.hasNext();) {
			ProductMarketingAttribute productMarketingAttribute = (ProductMarketingAttribute) iterator.next();
			productSkuViewBean.getGlobalAttributes().put(productMarketingAttribute.getAttributeDefinition().getCode(), productMarketingAttribute.getValueAsString());
		}
		
		Set<ProductMarketingAttribute> productMarketingMarketAreaAttributes = productSku.getProductMarketing().getProductMarketingMarketAreaAttributes();
		for (Iterator<ProductMarketingAttribute> iterator = productMarketingMarketAreaAttributes.iterator(); iterator.hasNext();) {
			ProductMarketingAttribute productMarketingAttribute = (ProductMarketingAttribute) iterator.next();
			productSkuViewBean.getMarketAreaAttributes().put(productMarketingAttribute.getAttributeDefinition().getCode(), productMarketingAttribute.getValueAsString());
		}
		
		Set<Asset> assets = productSku.getAssetsIsGlobal();
		for (Iterator<Asset> iterator = assets.iterator(); iterator.hasNext();) {
			Asset asset = (Asset) iterator.next();
			productSkuViewBean.getAssets().add(buildAssetViewBean(request, marketArea, localization, asset));
		}
		
		DateFormat dateFormat = requestUtil.getFormatDate(request, DateFormat.MEDIUM, DateFormat.MEDIUM);
		Date createdDate = productSku.getDateCreate();
		if(createdDate != null){
			productSkuViewBean.setCreatedDate(dateFormat.format(createdDate));
		} else {
			productSkuViewBean.setCreatedDate("NA");
		}
		Date updatedDate = productSku.getDateUpdate();
		if(updatedDate != null){
			productSkuViewBean.setUpdatedDate(dateFormat.format(updatedDate));
		} else {
			productSkuViewBean.setUpdatedDate("NA");
		}
		
//		productSkuViewBean.setBackgroundImage(productMarketing.getbackgroundImage);
//		productSkuViewBean.setCarouselImage(productMarketing.getcarouselImage);
//		productSkuViewBean.setIconImage(productMarketing.geticonImage);

//		productSkuViewBean.setProductSkuDetailsLabel(getSpecificMessage("business.product.sku.details.url.label", locale));
		productSkuViewBean.setProductSkuDetailsUrl(backofficeUrlService.buildProductSkuDetailsUrl(productSkuCode));

//		productSkuViewBean.setProductSkuEditLabel(getSpecificMessage("business.product.sku.edit.url.label", locale));
		productSkuViewBean.setProductSkuEditUrl(backofficeUrlService.buildProductSkuEditUrl(productSkuCode));

//		productSkuViewBean.setCancelLabel(getSpecificMessage(ScopeWebMessage.PRODUCT_SKU, "business.product.sku.edit.cancel.label", locale));
		final List<String> excludedPatterns = new ArrayList<String>();
		excludedPatterns.add("form");
		productSkuViewBean.setBackUrl(requestUtil.getLastRequestUrl(request, excludedPatterns));

//		productSkuViewBean.setSubmitLabel(getSpecificMessage(ScopeWebMessage.PRODUCT_SKU, "business.product.sku.edit.submit.label", locale));
		productSkuViewBean.setFormSubmitUrl(backofficeUrlService.buildProductSkuEditUrl(productSkuCode));
		
		return productSkuViewBean;
	}
	
	/**
     * 
     */
	public BrandViewBean buildBrandViewBean(final HttpServletRequest request, final ProductBrand productBrand) throws Exception {
		final BrandViewBean brandViewBean = new BrandViewBean();
		
		brandViewBean.setBusinessName(productBrand.getName());
		brandViewBean.setCode(productBrand.getCode());
//		brandViewBean.setBrandDetailsUrl(brandDetailsUrl);
//		brandViewBean.setBrandLineDetailsUrl(brandLineDetailsUrl);

		return brandViewBean;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public AssetViewBean buildAssetViewBean(HttpServletRequest request, MarketArea marketArea, Localization localization, Asset asset) throws Exception {
		final String assetCode = asset.getCode();
		
		AssetViewBean assetViewBean = new AssetViewBean();
		
		assetViewBean.setName(asset.getName());
		assetViewBean.setCode(assetCode);
		assetViewBean.setDescription(asset.getDescription());
		assetViewBean.setPath(asset.getPath());
		if(asset.getScope() != null){
			assetViewBean.setScope(asset.getScope().getPropertyKey());
		}
		if(asset.getType() != null){
			assetViewBean.setType(asset.getType().getPropertyKey());
		}
		if(asset.getSize() != null){
			assetViewBean.setSize(asset.getSize().getPropertyKey());
		}
		assetViewBean.setFileSize("" + asset.getFileSize());
		assetViewBean.setIsDefault("" + asset.isDefault());
		
		assetViewBean.setAbsoluteWebPath(requestUtil.getProductMarketingImageWebPath(request, asset));
		
		DateFormat dateFormat = requestUtil.getFormatDate(request, DateFormat.MEDIUM, DateFormat.MEDIUM);
		Date createdDate = asset.getDateCreate();
		if(createdDate != null){
			assetViewBean.setCreatedDate(dateFormat.format(createdDate));
		} else {
			assetViewBean.setCreatedDate("NA");
		}
		Date updatedDate = asset.getDateUpdate();
		if(updatedDate != null){
			assetViewBean.setUpdatedDate(dateFormat.format(updatedDate));
		} else {
			assetViewBean.setUpdatedDate("NA");
		}
		
		assetViewBean.setDetailsUrl(backofficeUrlService.buildAssetDetailsUrl(assetCode));
		assetViewBean.setEditUrl(backofficeUrlService.buildAssetEditUrl(assetCode));
		assetViewBean.setFormSubmitUrl(backofficeUrlService.buildAssetFormPostUrl());

		return assetViewBean;
	}

	/**
	 * @throws Exception 
	 * 
	 */
	public LegalTermsViewBean buildLegalTermsViewBean(final HttpServletRequest request, final Localization localization) throws Exception {
		final Locale locale = localization.getLocale();
		
		final LegalTermsViewBean legalTerms = new LegalTermsViewBean();
		
		legalTerms.setPageTitle(getSpecificMessage(ScopeWebMessage.LEGAL_TERMS, "header.title", locale));
		legalTerms.setTextHtml(getSpecificMessage(ScopeWebMessage.LEGAL_TERMS, "content.text", locale));

		legalTerms.setWarning(getCommonMessage(ScopeCommonMessage.LEGAL_TERMS, "warning", locale));
		legalTerms.setCopyright(getCommonMessage(ScopeCommonMessage.FOOTER, "copyright", locale));
		
		return legalTerms;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public SecurityViewBean buildSecurityViewBean(final HttpServletRequest request, final Localization localization) throws Exception {
		final Locale locale = localization.getLocale();
		final SecurityViewBean security = new SecurityViewBean();
		security.setLoginPageTitle(getSpecificMessage(ScopeWebMessage.AUTH, "header.title.login", locale));
		security.setLoginPageText(getSpecificMessage(ScopeWebMessage.AUTH, "login.content.text", locale));
		security.setForgottenPasswordPageText(getSpecificMessage(ScopeWebMessage.AUTH, "forgotten.password.content.text", locale));
		security.setLoginUrl(backofficeUrlService.buildSpringSecurityCheckUrl());
		security.setForgottenPasswordUrl(backofficeUrlService.buildForgottenPasswordUrl());
		return security;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public UserViewBean buildUserViewBean(final HttpServletRequest request, final Localization localization, final User user) throws Exception {
		final UserViewBean userViewBean = new UserViewBean();
		userViewBean.setId(user.getId());
		userViewBean.setLogin(user.getLogin());
		userViewBean.setFirstname(user.getFirstname());
		userViewBean.setLastname(user.getLastname());
		userViewBean.setEmail(user.getEmail());
		userViewBean.setPassword(user.getPassword());
		userViewBean.setActive(user.isActive());
		
		DateFormat dateFormat = requestUtil.getFormatDate(request, DateFormat.MEDIUM, DateFormat.MEDIUM);
		if(user.getDateCreate() != null){
			userViewBean.setDateCreate(dateFormat.format(user.getDateCreate()));
		} else {
			userViewBean.setDateCreate("NA");
		}
		if(user.getDateUpdate() != null){
			userViewBean.setDateUpdate(dateFormat.format(user.getDateUpdate()));
		} else {
			userViewBean.setDateUpdate("NA");
		}
		
		final Set<UserGroup> userGroups = user.getUserGroups();
		for (Iterator<UserGroup> iteratorUserGroup = userGroups.iterator(); iteratorUserGroup.hasNext();) {
			UserGroup userGroup = (UserGroup) iteratorUserGroup.next();
			String keyUserGroup = userGroup.getCode();
			String valueUserGroup = userGroup.getName();
			userViewBean.getUserGroups().put(keyUserGroup, valueUserGroup);
			
			final Set<UserRole> userRoles = userGroup.getGroupRoles();
			for (Iterator<UserRole> iteratorUserRole = userRoles.iterator(); iteratorUserRole.hasNext();) {
				UserRole userRole = (UserRole) iteratorUserRole.next();
				String keyUserRole = userRole.getCode() + " (" + userGroup.getCode() + ")";
				String valueUserRole = userRole.getName();
				userViewBean.getUserRoles().put(keyUserRole, valueUserRole);
				
				final Set<UserPermission> rolePermissions = userRole.getRolePermissions();
				for (Iterator<UserPermission> iteratorUserPermission = rolePermissions.iterator(); iteratorUserPermission.hasNext();) {
					UserPermission userPermission = (UserPermission) iteratorUserPermission.next();
					String keyUserPermission = userPermission.getCode() + " (" + userRole.getCode() + ")";
					String valueUserPermission = userPermission.getName();
					userViewBean.getUserPermissions().put(keyUserPermission, valueUserPermission);
				}
			}
		}
		
		final Set<UserConnectionLog> connectionLogs = user.getConnectionLogs();
		for (Iterator<UserConnectionLog> iteratorUserConnectionLog = connectionLogs.iterator(); iteratorUserConnectionLog.hasNext();) {
			UserConnectionLog userConnectionLog = (UserConnectionLog) iteratorUserConnectionLog.next();
			UserConnectionLogValueBean userConnectionLogValueBean = new UserConnectionLogValueBean();
			userConnectionLogValueBean.setDate(dateFormat.format(userConnectionLog.getLoginDate()));
			if(StringUtils.isNotEmpty(userConnectionLog.getHost())){
				userConnectionLogValueBean.setHost(userConnectionLog.getHost());
			} else {
				userConnectionLogValueBean.setHost("NA");
			}
			if(StringUtils.isNotEmpty(userConnectionLog.getAddress())){
				userConnectionLogValueBean.setAddress(userConnectionLog.getAddress());
			} else {
				userConnectionLogValueBean.setAddress("NA");
			}
			userViewBean.getUserConnectionLogs().add(userConnectionLogValueBean);
		}

		final List<String> excludedPatterns = new ArrayList<String>();
		excludedPatterns.add("form");
		userViewBean.setBackUrl(requestUtil.getLastRequestUrl(request, excludedPatterns));
		userViewBean.setUserDetailsUrl(backofficeUrlService.buildUserDetailsUrl(user.getId().toString()));
		userViewBean.setUserEditUrl(backofficeUrlService.buildUserEditUrl(user.getId().toString()));
		
		return userViewBean;
	}

	/**
     * 
     */
	public List<UserViewBean> buildUserViewBeans(final HttpServletRequest request, final Localization localization, final List<User> users) throws Exception {
		final List<UserViewBean> userViewBeans = new ArrayList<UserViewBean>();
		for (Iterator<User> iterator = users.iterator(); iterator.hasNext();) {
			User user = (User) iterator.next();
			userViewBeans.add(buildUserViewBean(request, localization, user));
		}
		return userViewBeans;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public ShippingViewBean buildShippingViewBean(final HttpServletRequest request, final Localization localization, final Shipping shipping) throws Exception{
		ShippingViewBean shippingViewBean = new ShippingViewBean();
		shippingViewBean.setId(shipping.getId());
		
		shippingViewBean.setVersion(shipping.getVersion());
		shippingViewBean.setName(shipping.getName());
		shippingViewBean.setDescription(shipping.getDescription());
		shippingViewBean.setCode(shipping.getCode());
		shippingViewBean.setPrice(shipping.getPrice());

		shippingViewBean.setMarketAreaId(shipping.getMarketAreaId());
		
		DateFormat dateFormat = requestUtil.getFormatDate(request, DateFormat.MEDIUM, DateFormat.MEDIUM);
		if(shipping.getDateCreate() != null){
			shippingViewBean.setDateCreate(dateFormat.format(shipping.getDateCreate()));
		} else {
			shippingViewBean.setDateCreate("NA");
		}
		if(shipping.getDateUpdate() != null){
			shippingViewBean.setDateUpdate(dateFormat.format(shipping.getDateUpdate()));
		} else {
			shippingViewBean.setDateUpdate("NA");
		}
		
		shippingViewBean.setDetailsUrl(backofficeUrlService.buildCustomerDetailsUrl(shipping.getCode()));
		shippingViewBean.setEditUrl(backofficeUrlService.buildCustomerEditUrl(shipping.getCode()));

		return shippingViewBean;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public OrderViewBean buildOrderViewBean(final HttpServletRequest request, final Localization localization, final Order order) throws Exception{
		OrderViewBean orderViewBean = new OrderViewBean();
		orderViewBean.setId(order.getId());
		
		orderViewBean.setVersion(order.getVersion());
		orderViewBean.setStatus(order.getStatus());
		orderViewBean.setOrderNum(order.getOrderNum());
		orderViewBean.setCustomerId(order.getCustomerId());
		orderViewBean.setBillingAddressId(order.getBillingAddressId());
		orderViewBean.setShippingAddressId(order.getShippingAddressId());
		
		DateFormat dateFormat = requestUtil.getFormatDate(request, DateFormat.MEDIUM, DateFormat.MEDIUM);
		if(order.getDateCreate() != null){
			orderViewBean.setDateCreate(dateFormat.format(order.getDateCreate()));
		} else {
			orderViewBean.setDateCreate("NA");
		}
		if(order.getDateUpdate() != null){
			orderViewBean.setDateUpdate(dateFormat.format(order.getDateUpdate()));
		} else {
			orderViewBean.setDateUpdate("NA");
		}
		
		orderViewBean.setDetailsUrl(backofficeUrlService.buildOrderDetailsUrl(order.getOrderNum()));
		orderViewBean.setEditUrl(backofficeUrlService.buildOrderEditUrl(order.getOrderNum()));

		return orderViewBean;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public RuleViewBean buildRuleViewBean(final HttpServletRequest request, final Localization localization, final AbstractRuleReferential rule) throws Exception{
		RuleViewBean ruleViewBean = new RuleViewBean();
		ruleViewBean.setId(rule.getId());
		
		ruleViewBean.setVersion(rule.getVersion());
		ruleViewBean.setName(rule.getName());
		ruleViewBean.setDescription(rule.getDescription());
		ruleViewBean.setSalience(rule.getSalience());
		
		DateFormat dateFormat = requestUtil.getFormatDate(request, DateFormat.MEDIUM, DateFormat.MEDIUM);
		if(rule.getDateCreate() != null){
			ruleViewBean.setDateCreate(dateFormat.format(rule.getDateCreate()));
		} else {
			ruleViewBean.setDateCreate("NA");
		}
		if(rule.getDateUpdate() != null){
			ruleViewBean.setDateUpdate(dateFormat.format(rule.getDateUpdate()));
		} else {
			ruleViewBean.setDateUpdate("NA");
		}
		
		ruleViewBean.setDetailsUrl(backofficeUrlService.buildRuleDetailsUrl(rule.getCode()));
		ruleViewBean.setEditUrl(backofficeUrlService.buildRuleEditUrl(rule.getCode()));

		return ruleViewBean;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public CustomerViewBean buildCustomerViewBean(final HttpServletRequest request, final Localization localization, final Customer customer) throws Exception{
		CustomerViewBean customerViewBean = new CustomerViewBean();
		customerViewBean.setId(customer.getId());

		customerViewBean.setVersion(customer.getVersion());
		customerViewBean.setLogin(customer.getLogin());
		customerViewBean.setTitle(customer.getTitle());
		customerViewBean.setFirstname(customer.getFirstname());
		customerViewBean.setLastname(customer.getLastname());
		customerViewBean.setEmail(customer.getEmail());
		customerViewBean.setPassword(customer.getPassword());
		customerViewBean.setDefaultLocale(customer.getDefaultLocale());
		customerViewBean.setActive(customer.isActive());
		
		DateFormat dateFormat = requestUtil.getFormatDate(request, DateFormat.MEDIUM, DateFormat.MEDIUM);
		if(customer.getDateCreate() != null){
			customerViewBean.setDateCreate(dateFormat.format(customer.getDateCreate()));
		} else {
			customerViewBean.setDateCreate("NA");
		}
		if(customer.getDateUpdate() != null){
			customerViewBean.setDateUpdate(dateFormat.format(customer.getDateUpdate()));
		} else {
			customerViewBean.setDateUpdate("NA");
		}
		
		customerViewBean.setDetailsUrl(backofficeUrlService.buildCustomerDetailsUrl(customer.getCode()));
		customerViewBean.setEditUrl(backofficeUrlService.buildCustomerEditUrl(customer.getCode()));

		return customerViewBean;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public QuickSearchViewBean buildQuickSearchViewBean(final HttpServletRequest request, final Localization localization) throws Exception {
		final QuickSearchViewBean quickSearch = new QuickSearchViewBean();
//		quickSearch.setTextLabel(getSpecificMessage("form.search.label.text", locale));
		quickSearch.setUrlFormSubmit(backofficeUrlService.buildGlobalSearchUrl());
		return quickSearch;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public List<GlobalSearchViewBean> buildGlobalSearchViewBean(final HttpServletRequest request, final MarketArea marketArea, 
																final Localization localization, final Retailer retailer, final String searchText) throws Exception {
		
		final List<GlobalSearchViewBean> globalSearchViewBeans = new ArrayList<GlobalSearchViewBean>();

		final List<ProductMarketing> productMarketings = productMarketingService.findProductMarketings(marketArea.getId(), retailer.getId(), searchText);
		for (Iterator<ProductMarketing> iterator = productMarketings.iterator(); iterator.hasNext();) {
			ProductMarketing productMarketing = (ProductMarketing) iterator.next();
			
			final GlobalSearchViewBean globalSearchViewBean = new GlobalSearchViewBean();
			globalSearchViewBean.setValue(productMarketing.getBusinessName() + " : " + productMarketing.getCode());
			globalSearchViewBean.setType("ProductMarketing");
			globalSearchViewBean.setUrl(backofficeUrlService.buildProductMarketingDetailsUrl(productMarketing.getCode()));

			globalSearchViewBeans.add(globalSearchViewBean);
		}
		
		final List<ProductSku> productSkus = productSkuService.findProductSkus(marketArea.getId(), retailer.getId(), searchText);
		for (Iterator<ProductSku> iterator = productSkus.iterator(); iterator.hasNext();) {
			ProductSku productSku = (ProductSku) iterator.next();
			
			final GlobalSearchViewBean globalSearchViewBean = new GlobalSearchViewBean();
			globalSearchViewBean.setValue(productSku.getBusinessName() + " : " + productSku.getCode());
			globalSearchViewBean.setType("ProductSku");
			globalSearchViewBean.setUrl(backofficeUrlService.buildProductSkuDetailsUrl(productSku.getCode()));

			globalSearchViewBeans.add(globalSearchViewBean);
		}

		return globalSearchViewBeans;
	}
	
	/**
     * 
     */
	public List<EngineSettingViewBean> buildEngineSettingViewBeans(final HttpServletRequest request, final List<EngineSetting> engineSettings) throws Exception {
		final List<EngineSettingViewBean> engineSettingViewBeans = new ArrayList<EngineSettingViewBean>();
		for (Iterator<EngineSetting> iterator = engineSettings.iterator(); iterator.hasNext();) {
			EngineSetting engineSetting = (EngineSetting) iterator.next();
			engineSettingViewBeans.add(buildEngineSettingViewBean(request, engineSetting));
		}
		return engineSettingViewBeans;
	}
	
	/**
     * 
     */
	public EngineSettingViewBean buildEngineSettingViewBean(final HttpServletRequest request, final EngineSetting engineSetting) throws Exception {
		final EngineSettingViewBean engineSettingViewBean = new EngineSettingViewBean();
		engineSettingViewBean.setName(engineSetting.getName());
		engineSettingViewBean.setDescription(engineSetting.getDescription());
		engineSettingViewBean.setEngineSettingDetailsUrl(backofficeUrlService.buildEngineSettingDetailsUrl(engineSetting.getId().toString()));
		Set<EngineSettingValue> engineSettingValues = engineSetting.getEngineSettingValues();
		if(engineSettingValues != null){
			for (Iterator<EngineSettingValue> iterator = engineSettingValues.iterator(); iterator.hasNext();) {
				EngineSettingValue engineSettingValue = (EngineSettingValue) iterator.next();
				engineSettingViewBean.getEngineSettingValues().add(buildEngineSettingValueViewBean(request, engineSettingValue));
			}
		}
		return engineSettingViewBean;
	}
	
	/**
     * 
     */
	public EngineSettingDetailsViewBean buildEngineSettingDetailsViewBean(final HttpServletRequest request, final Localization localization) throws Exception {
		final Locale locale = localization.getLocale();
		final EngineSettingDetailsViewBean engineSettingValueDetailsViewBean = new EngineSettingDetailsViewBean();
//		engineSettingValueDetailsViewBean.setEditLabel(coreMessageSource.getMessage("engine.setting.details.edit.value", null, locale));
		return engineSettingValueDetailsViewBean;
	}
	
	/**
     * 
     */
	public EngineSettingValueViewBean buildEngineSettingValueViewBean(final HttpServletRequest request, final EngineSettingValue engineSettingValue) throws Exception {
		final EngineSettingValueViewBean engineSettingValueViewBean = new EngineSettingValueViewBean();
		engineSettingValueViewBean.setContext(engineSettingValue.getContext());
		engineSettingValueViewBean.setValue(engineSettingValue.getValue());
		engineSettingValueViewBean.setEditUrl(backofficeUrlService.buildEngineSettingValueEditUrl(engineSettingValue.getId().toString()));
		return engineSettingValueViewBean;
	}
	
	/**
     * 
     */
	public EngineSettingValueEditViewBean buildEngineSettingValueEditViewBean(final HttpServletRequest request, final Localization localization, final EngineSettingValue engineSettingValue) throws Exception {
		final Locale locale = localization.getLocale();
		final EngineSettingValueEditViewBean engineSettingValueEdit = new EngineSettingValueEditViewBean();
//		engineSettingValueEdit.setSubmitLabel(coreMessageSource.getMessage("form.engine.setting.value.edit.submit.label", null, locale));
		engineSettingValueEdit.setFormSubmitUrl(backofficeUrlService.buildEngineSettingValueEditUrl(engineSettingValue.getId().toString()));
		return engineSettingValueEdit;
	}
	
	/**
     * 
     */
	public List<BatchViewBean> buildBatchViewBeans(final HttpServletRequest request, final Localization localization, final List<BatchProcessObject> batchProcessObjects) throws Exception {
		final List<BatchViewBean> batchViewBeans = new ArrayList<BatchViewBean>();
		for (Iterator<BatchProcessObject> iterator = batchProcessObjects.iterator(); iterator.hasNext();) {
			BatchProcessObject batchProcessObject = (BatchProcessObject) iterator.next();
			batchViewBeans.add(buildBatchViewBean(request, localization, batchProcessObject));
		}
		return batchViewBeans;
	}
	
	/**
     * 
     */
	public BatchViewBean buildBatchViewBean(final HttpServletRequest request, final Localization localization, final BatchProcessObject batchProcessObject) throws Exception {
		final BatchViewBean batchViewBean = new BatchViewBean();
		batchViewBean.setId(batchProcessObject.getId());
		batchViewBean.setStatus(batchProcessObject.getStatus());
		batchViewBean.setTypeObject(batchProcessObject.getTypeObject().getPropertyKey());
		batchViewBean.setProcessedCount(batchProcessObject.getProcessedCount());
		return batchViewBean;
	}

	/**
     * 
     */
	public List<CurrencyReferentialViewBean> buildCurrencyReferentialViewBeans(final HttpServletRequest request, final List<CurrencyReferential> currencyReferentials) throws Exception {
		final List<CurrencyReferentialViewBean> currencyReferentialViewBeans = new ArrayList<CurrencyReferentialViewBean>();
		if(currencyReferentials != null){
			for (Iterator<CurrencyReferential> iterator = currencyReferentials.iterator(); iterator.hasNext();) {
				CurrencyReferential currencyReferential = (CurrencyReferential) iterator.next();
				currencyReferentialViewBeans.add(buildCurrencyReferentialViewBean(request, currencyReferential));
			}
		}
		return currencyReferentialViewBeans;
	}
	
	/**
     * 
     */
	public CurrencyReferentialViewBean buildCurrencyReferentialViewBean(final HttpServletRequest request, final CurrencyReferential currencyReferential) throws Exception {
		final CurrencyReferentialViewBean currencyReferentialViewBean = new CurrencyReferentialViewBean();
		if(currencyReferential != null){
			currencyReferentialViewBean.setName(currencyReferential.getName());
			currencyReferentialViewBean.setDescription(currencyReferential.getDescription());
			currencyReferentialViewBean.setCode(currencyReferential.getCode());
			currencyReferentialViewBean.setSign(currencyReferential.getSign());
			currencyReferentialViewBean.setAbbreviated(currencyReferential.getAbbreviated());
			
			DateFormat dateFormat = requestUtil.getFormatDate(request, DateFormat.MEDIUM, DateFormat.MEDIUM);
			Date dateCreate = currencyReferential.getDateCreate();
			if(dateCreate != null){
				currencyReferentialViewBean.setDateCreate(dateFormat.format(dateCreate));
			} else {
				currencyReferentialViewBean.setDateCreate("NA");
			}
			
			Date dateUpdate = currencyReferential.getDateUpdate();
			if(dateUpdate != null){
				currencyReferentialViewBean.setDateUpdate(dateFormat.format(dateUpdate));
			} else {
				currencyReferentialViewBean.setDateUpdate("NA");
			}
			
		}
		return currencyReferentialViewBean;
	}
	
	/**
     * 
     */
	public List<PaymentGatewayViewBean> buildPaymentGatewayViewBeans(final HttpServletRequest request, final List<AbstractPaymentGateway> paymentGateways) throws Exception {
		final List<PaymentGatewayViewBean> paymentGatewayViewBeans = new ArrayList<PaymentGatewayViewBean>();
		if(paymentGateways != null){
			for (Iterator<AbstractPaymentGateway> iterator = paymentGateways.iterator(); iterator.hasNext();) {
				AbstractPaymentGateway paymentGateway = (AbstractPaymentGateway) iterator.next();
				paymentGatewayViewBeans.add(buildPaymentGatewayViewBean(request, paymentGateway));
			}
		}
		return paymentGatewayViewBeans;
	}
	
	/**
     * 
     */
	public PaymentGatewayViewBean buildPaymentGatewayViewBean(final HttpServletRequest request, final AbstractPaymentGateway paymentGateway) throws Exception {
		final PaymentGatewayViewBean paymentGatewayViewBean = new PaymentGatewayViewBean();
		if(paymentGateway != null){
			paymentGatewayViewBean.setName(paymentGateway.getName());
			paymentGatewayViewBean.setDescription(paymentGateway.getDescription());
			paymentGatewayViewBean.setCode(paymentGateway.getCode());
			
			DateFormat dateFormat = requestUtil.getFormatDate(request, DateFormat.MEDIUM, DateFormat.MEDIUM);
			Date dateCreate = paymentGateway.getDateCreate();
			if(dateCreate != null){
				paymentGatewayViewBean.setDateCreate(dateFormat.format(dateCreate));
			} else {
				paymentGatewayViewBean.setDateCreate("NA");
			}
			
			Date dateUpdate = paymentGateway.getDateUpdate();
			if(dateUpdate != null){
				paymentGatewayViewBean.setDateUpdate(dateFormat.format(dateUpdate));
			} else {
				paymentGatewayViewBean.setDateUpdate("NA");
			}
			
		}
		return paymentGatewayViewBean;
	}
	
//	/**
//     * 
//     */
//	public UserViewBean buildUserViewBean(final HttpServletRequest request, final Localization localization, final User user) throws Exception {
//		final Locale locale = localization.getLocale();
//		final UserViewBean userEditViewBean = new UserViewBean();
//
//		userEditViewBean.setId(user.getId());
//		userEditViewBean.setLoginLabel(getSpecificMessage("user.login.label", locale));
//		userEditViewBean.setFirstnameLabel(getSpecificMessage("user.firstname.label", locale));
//		userEditViewBean.setLastnameLabel(getSpecificMessage("user.lastname.label", locale));
//		userEditViewBean.setEmailLabel(getSpecificMessage("user.email.label", locale));
//		userEditViewBean.setPasswordLabel(getSpecificMessage("user.password.label", locale));
//		userEditViewBean.setActiveLabel(getSpecificMessage("user.active.label", locale));
//		if(user.isActive()){
//			userEditViewBean.setActiveValueLabel(getSpecificMessage("user.active.value.true", locale));
//		} else {
//			userEditViewBean.setActiveValueLabel(getSpecificMessage("user.active.value.false", locale));
//		}
//
//		
//		userEditViewBean.setDateCreateLabel(getSpecificMessage("user.date.create.label", locale));
//		userEditViewBean.setDateUpdateLabel(getSpecificMessage("user.date.update.label", locale));
//		
//		
//		return userEditViewBean;
//	}
	
}